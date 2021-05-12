package com.xxl.db;

import com.codingapi.txlcn.common.util.Maps;
import com.codingapi.txlcn.tc.core.transaction.txc.analy.def.PrimaryKeysProvider;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class MysqlPrimaryKeysProvider implements PrimaryKeysProvider {

    @Override
    public Map<String, List<String>> provide() {
        return Maps.of("t_demo", Collections.singletonList("kid"));
    }
}
