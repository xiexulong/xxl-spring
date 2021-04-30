package com.xxl.service;

import com.xxl.entity.User;

public interface UserService {
    User findByName(String userName);
}
