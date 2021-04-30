package com.xxl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaServer
@ComponentScan("com.xxl")
public class RegisterCenter
{
    public static void main(String[] args) {

        //  http://127.0.0.1:9910/
        SpringApplication.run(RegisterCenter.class, args);
    }
}
