package com.xxl.controller;

import com.xxl.entity.User;
import com.xxl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/findUser")
    public List<User> findUser() {
        return userService.findUser();
    }

    @RequestMapping("/insertUser")
    public List<User> insertUser(String name) {
        return userService.insertUser(name);
    }

}
