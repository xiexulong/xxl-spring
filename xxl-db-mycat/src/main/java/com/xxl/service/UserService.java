package com.xxl.service;

import com.xxl.entity.User;
import com.xxl.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public List<User> findUser() {
        return userMapper.findUser();
    }

    public List<User> insertUser(String name) {
        return userMapper.insertUser(name);
    }
}
