package com.xxl;

import com.codingapi.txlcn.tc.config.EnableDistributedTransaction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableDistributedTransaction
public class SpringServiceBApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringServiceBApplication.class, args);

    }
}
