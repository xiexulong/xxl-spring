package com.xxl.db.mapper;

import com.xxl.db.domain.Demo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DemoMapper {

    @Insert("insert into t_demo(kid, demo_field, group_id, create_time,app_name) values(#{kid}, #{demoField}, #{groupId}, #{createTime},#{appName})")
    void save(Demo demo);

    @Delete("delete from t_demo where id=#{id}")
    void deleteByKId(Long id);
}
