package com.xxl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableZuulProxy
@SpringBootApplication
@EnableAsync
@EnableDiscoveryClient
public class GatewayZuul {
    public static void main(String[] args) {
        SpringApplication.run(GatewayZuul.class, args);
    }
}
