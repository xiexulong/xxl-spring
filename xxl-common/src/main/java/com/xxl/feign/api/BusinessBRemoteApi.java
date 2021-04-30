package com.xxl.feign.api;

import com.xxl.feign.conf.FeignConfig;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(value = "xxl-business-b", configuration = FeignConfig.class)
public interface BusinessBRemoteApi {

    @RequestMapping(value = "/BussinessB/helloFeign", method = RequestMethod.GET)
    String helloFeign();

}
