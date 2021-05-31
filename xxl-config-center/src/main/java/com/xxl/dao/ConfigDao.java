
package com.xxl.dao;

import java.util.ArrayList;
import java.util.List;

import com.xxl.mapper.ConfigRowMapper;
import com.xxl.model.Config;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;


@Repository
public class ConfigDao {

    private final JdbcTemplate jdbc;
    
    public ConfigDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * query configs.
     * @param application application type.
     * @param item config item.
     * @param favorite is favorite, 0 no, 1 yes.
     * @return list of Config {@link Config}.
     */
    public List<Config> getList(String application, String item, Integer favorite, Integer type) {
        StringBuilder sb = new StringBuilder();
        List<Object> parameters = new ArrayList<>();
        sb.append("select * from config where 1=1");
        if (!StringUtils.isEmpty(application)) {
            sb.append(" and application = ?");
            parameters.add(application);
        }
        if (!StringUtils.isEmpty(item)) {
            sb.append(" and item = ?");
            parameters.add(item);
        }
        if (favorite != null) {
            sb.append(" and favorite = ?");
            parameters.add(favorite);
        }
        if (type != null) {
            sb.append(" and type = ?");
            parameters.add(type);
        }
        if (parameters.size() > 0) {
            return jdbc.query(sb.toString(), parameters.toArray(), new ConfigRowMapper());
        }
        return jdbc.query(sb.toString(), new ConfigRowMapper());
    }

    public List<Config> getList(String application, String item, Integer favorite) {
        return getList(application,item,favorite, null);
    }

    public int update(long id, String value) {
        String sql = "update config set value = ? where id = ?";
        return jdbc.update(sql, value, id);
    }

    public Config queryById(long id) {
        String sql = "select * from config where id = ?";
        return jdbc.queryForObject(sql, new ConfigRowMapper(), id);
    }
}
