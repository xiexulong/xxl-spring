package com.xxl.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class XxlController {
    private static final Logger logger = LoggerFactory.getLogger(XxlController.class);

    @Value("${xxl.bus.app}")
    public String busValue;


    @GetMapping("/hello")
    public String getHello() throws Exception {
        Thread.sleep(100);
        logger.info("call business-a getHello(), response hello xxl");
        return "hello xxl!" + busValue;
    }

}
