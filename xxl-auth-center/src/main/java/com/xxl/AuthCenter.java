package com.xxl;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableCaching
@EnableDiscoveryClient
@ComponentScan("com.xxl")
public class AuthCenter {

    public static void main(String[] args) throws BeansException {
        SpringApplication.run(AuthCenter.class, args);
    }
}
