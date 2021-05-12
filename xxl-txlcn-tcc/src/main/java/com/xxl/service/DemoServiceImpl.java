package com.xxl.service;

import com.codingapi.txlcn.common.util.Transactions;
import com.codingapi.txlcn.tc.annotation.DTXPropagation;
import com.codingapi.txlcn.tc.annotation.TccTransaction;
import com.codingapi.txlcn.tracing.TracingContext;
import com.google.common.collect.Sets;
import com.xxl.db.domain.Demo;
import com.xxl.db.mapper.DemoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DemoServiceImpl implements DemoService {
    private static final Logger logger = LoggerFactory.getLogger(DemoServiceImpl.class);

    private final DemoMapper demoMapper;

    private ConcurrentHashMap<String, Set<Long>> ids = new ConcurrentHashMap<>();

    @Autowired
    public DemoServiceImpl(DemoMapper demoMapper) {
        this.demoMapper = demoMapper;
    }

    @Override
    @TccTransaction(propagation = DTXPropagation.SUPPORTS)
    @Transactional
    public String rpc(String value) {
        Demo demo = new Demo();
        demo.setDemoField(value);
        demo.setCreateTime(new Date());
        demo.setAppName(Transactions.getApplicationId());
        demo.setGroupId(TracingContext.tracing().groupId());
        demoMapper.save(demo);
        ids.putIfAbsent(TracingContext.tracing().groupId(), Sets.newHashSet(demo.getId()));
        ids.get(TracingContext.tracing().groupId()).add(demo.getId());
        return "ok-service-c";
    }

    public void confirmRpc(String value) {
        ids.get(TracingContext.tracing().groupId()).forEach(id -> {
            logger.info("tcc-confirm-{}-{}" + TracingContext.tracing().groupId(), id);
            ids.get(TracingContext.tracing().groupId()).remove(id);
        });
    }

    public void cancelRpc(String value) {
        ids.get(TracingContext.tracing().groupId()).forEach(id -> {
            logger.info("tcc-cancel-{}-{}", TracingContext.tracing().groupId(), id);
            demoMapper.deleteByKId(id);
        });
    }
}
