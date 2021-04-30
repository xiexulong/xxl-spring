package com.xxl.feign.conf;

import feign.Retryer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.concurrent.TimeUnit.SECONDS;

@Configuration
public class FeignConfig {

    @Value("${global.feign.client.config.maxAttempts}")
    private int maxAttempts;

    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(1000, SECONDS.toMillis(2), maxAttempts);
    }

}
