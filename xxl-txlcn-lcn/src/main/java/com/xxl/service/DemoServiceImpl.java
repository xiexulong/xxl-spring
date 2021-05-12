package com.xxl.service;

import com.codingapi.txlcn.common.util.Transactions;
import com.codingapi.txlcn.tracing.TracingContext;
import com.xxl.db.domain.Demo;
import com.xxl.db.mapper.DemoMapper;
import com.xxl.feign.api.TxlcnTccRemoteApi;
import com.xxl.feign.api.TxlcnTxcRemoteApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Objects;

/**
 * Description:
 * Date: 2018/12/25
 *
 * @author ujued
 */
@Service
public class DemoServiceImpl implements DemoService {

    private final DemoMapper demoMapper;

    private final TxlcnTccRemoteApi txlcnTccRemoteApi;

    private final TxlcnTxcRemoteApi txlcnTxcRemoteApi;

    private final RestTemplate restTemplate;

    @Autowired
    public DemoServiceImpl(DemoMapper demoMapper, TxlcnTccRemoteApi txlcnTccRemoteApi, TxlcnTxcRemoteApi txlcnTxcRemoteApi, RestTemplate restTemplate) {
        this.demoMapper = demoMapper;
        this.txlcnTccRemoteApi = txlcnTccRemoteApi;
        this.txlcnTxcRemoteApi = txlcnTxcRemoteApi;

        this.restTemplate = restTemplate;
    }

    @Override
    public String execute(String value, String exFlag) {
        // step1. call remote ServiceD
//        String dResp = txlcnTccRemoteApi.rpc(value);

        String dResp = restTemplate.getForObject("http://127.0.0.1:12002/rpc?value=" + value, String.class);

        // step2. call remote ServiceE
        String eResp = txlcnTccRemoteApi.rpc(value);

        // step3. execute local transaction
        Demo demo = new Demo();
        demo.setGroupId(TracingContext.tracing().groupId());
        demo.setDemoField(value);
        demo.setCreateTime(new Date());
        demo.setAppName(Transactions.getApplicationId());
        demoMapper.save(demo);

        // 置异常标志，DTX 回滚
        if (Objects.nonNull(exFlag)) {
            throw new IllegalStateException("by exFlag");
        }

        return dResp + " > " + eResp + " > " + "ok-service-a";
    }
}
