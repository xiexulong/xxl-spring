package com.xxl;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableFeignClients(basePackages = "com.xxl.feign.api")
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan("com.xxl")
public class BusinessA {

    public static void main(String[] args) {
        SpringApplication.run(BusinessA.class, args);
    }

}