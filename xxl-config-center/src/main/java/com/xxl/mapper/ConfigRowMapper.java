package com.xxl.mapper;

import com.xxl.enums.ConfigType;
import com.xxl.model.Config;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ConfigRowMapper implements RowMapper<Config> {
    @Override
    public Config mapRow(ResultSet rs, int i) throws SQLException {
        Config config = new Config();
        config.setId(rs.getLong("id"));
        config.setApplication(rs.getString("application"));
        config.setItem(rs.getString("item"));
        config.setType(ConfigType.findDisplay(rs.getString("type")));
        config.setLabel(rs.getString("label"));
        config.setProfile(rs.getString("profile"));
        config.setRemark(rs.getString("remark"));
        config.setValue(rs.getString("value"));
        config.setFavorite(rs.getInt("favorite"));
        return config;
    }
}
