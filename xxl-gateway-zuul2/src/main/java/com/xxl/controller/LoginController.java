package com.xxl.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {


    @GetMapping(value = "/login")
    public String loginByPassword() {
        //此处省略具体的登录逻辑
        return "登录成功！";
    }

    @GetMapping(value = "/hello")
    public String hello() {
        //此处省略具体的登录逻辑
        return "hello xxl！";
    }
}
