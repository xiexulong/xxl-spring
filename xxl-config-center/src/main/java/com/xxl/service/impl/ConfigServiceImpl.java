
package com.xxl.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.xxl.dao.ConfigDao;
import com.xxl.enums.ConfigType;
import com.xxl.model.Config;
import com.xxl.service.ConfigService;
import org.springframework.cloud.bus.endpoint.RefreshBusEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ConfigServiceImpl implements ConfigService {
    private final ConfigDao dao;
    private final Optional<RefreshBusEndpoint> refreshBusEndpoint;

    public ConfigServiceImpl(ConfigDao dao, Optional<RefreshBusEndpoint> refreshBusEndpoint) {
        this.dao = dao;
        this.refreshBusEndpoint = refreshBusEndpoint;
    }

    @Override
    public List<Config> list(String application, String item, Integer favorite) {
        return dao.getList(application, item, favorite);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Map<Long, String> configMap) {
        List<Long> ids = new ArrayList<>(configMap.keySet());
        ids.sort(null);
        for (Long id : ids) {
            Config config = dao.queryById(id);



            if (ConfigType.READ_ONLY.getIntValue() == config.getType().getIntValue()) {
                throw new IllegalArgumentException("Can't modify read-only item!");
            }
            int num = dao.update(id, configMap.get(id));

        }
        if (refreshBusEndpoint.isPresent()) {
            refreshBusEndpoint.get().refresh(null);
        }
    }


}
