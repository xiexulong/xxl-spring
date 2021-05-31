package com.xxl.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.xxl.controller.XxlController;
import com.xxl.exception.HttpBadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @HystrixCommand(fallbackMethod = "getDefaultMessage" ,ignoreExceptions = {NullPointerException.class})
    public String getMessage(long id) {
        logger.info("getMessage() id:{}", id);
        if (id == 1) {
            throw new IndexOutOfBoundsException();
        } else if (id == 2) {
            throw new NullPointerException();
        }

        return "hello world!";
    }

    public String getDefaultMessage(long id) {
        return "稍后访问!";
    }
}
