package com.xxl.feign.api.impl;

import com.xxl.feign.api.BusinessARemoteApi;
import org.springframework.stereotype.Component;

@Component
public class BusinessAFallback implements BusinessARemoteApi {
    @Override
    public String hello() {
        return "error info!";
    }
}
