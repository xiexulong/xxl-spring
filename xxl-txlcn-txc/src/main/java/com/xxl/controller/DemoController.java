package com.xxl.controller;

import com.xxl.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class DemoController {

    @Autowired
    private DemoService demoService;

    @GetMapping("/rpc")
    public String rpc(@RequestParam("value") String value, HttpServletRequest servletRequest) {
        return demoService.rpc(value);
    }
}
