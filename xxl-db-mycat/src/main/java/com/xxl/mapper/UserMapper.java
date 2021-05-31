package com.xxl.mapper;

import com.xxl.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper {

    @Select("select * from mycat_user")
    public List<User> findUser();

    @Select("insert into mycat_user values(#{name});")
    public List<User> insertUser(@Param("name") String name);
}
