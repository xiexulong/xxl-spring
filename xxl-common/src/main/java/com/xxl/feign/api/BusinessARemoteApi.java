package com.xxl.feign.api;

import com.xxl.feign.api.impl.BusinessAFallback;
import com.xxl.feign.conf.FeignConfig;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(value = "xxl-business-a", configuration = FeignConfig.class, fallback = BusinessAFallback.class)
public interface BusinessARemoteApi {
    @RequestMapping(value = "/BussinessA/hello", method = RequestMethod.GET)
    String hello();
}
