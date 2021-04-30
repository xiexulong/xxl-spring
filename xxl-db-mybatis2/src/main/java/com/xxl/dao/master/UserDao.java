package com.xxl.dao.master;

import com.xxl.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserDao {

    User findByName(@Param("userName") String username);
}
