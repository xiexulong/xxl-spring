package com.xxl.feign.api;

import com.xxl.feign.conf.FeignConfig;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "xxl-txlcn-tcc", configuration = FeignConfig.class)
public interface TxlcnTccRemoteApi {

    @GetMapping("/rpc")
    String rpc(@RequestParam("value") String name);
}
