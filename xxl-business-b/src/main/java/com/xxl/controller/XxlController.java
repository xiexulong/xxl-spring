package com.xxl.controller;

import com.xxl.feign.api.BusinessARemoteApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class XxlController {
    private static final Logger logger = LoggerFactory.getLogger(XxlController.class);

    @Autowired
    private BusinessARemoteApi businessARemoteApi;

    @GetMapping("/hello")
    public String getHello() {

        return "hello xxl b!";
    }


    @GetMapping("/helloFeign")
    public String getHelloFeign() {
        logger.info("call businessARemoteApi.hello()");
        return businessARemoteApi.hello();
    }
}
