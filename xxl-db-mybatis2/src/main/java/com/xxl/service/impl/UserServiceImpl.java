package com.xxl.service.impl;

import com.xxl.dao.cluster.CityDao;
import com.xxl.dao.master.UserDao;
import com.xxl.entity.City;
import com.xxl.entity.User;
import com.xxl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private CityDao cityDao;

    @Override
    public User findByName(String userName) {
        User user = userDao.findByName(userName);
        City city = cityDao.findByName("aa");
        user.setCity(city);
        return user;
    }
}
