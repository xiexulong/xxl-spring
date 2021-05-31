package com.xxl.controller;

import com.xxl.entity.vo.AlgorithmConfigVO;
import com.xxl.feign.api.BusinessBRemoteApi;
import com.xxl.service.UserService;
import com.xxl.util.FileUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class XxlController {
    private static final Logger logger = LoggerFactory.getLogger(XxlController.class);


    @Autowired
    private BusinessBRemoteApi businessBRemoteApi;


    @GetMapping("/hello")
    public String getHello() throws Exception {
        Thread.sleep(100);
        logger.info("call business-a getHello(), response hello xxl");
        return "hello xxl!";
    }

    @GetMapping("/helloFeign")
    public String getHelloFeign() {
        logger.info("call businessBRemoteApi.helloFeign()");
        return businessBRemoteApi.helloFeign();
    }

    @Autowired
    public UserService userService;

    /**
     * http://192.168.0.107:9060/BussinessA/hystrixMessage?id=1
     * http://192.168.0.107:9060/BussinessA/hystrixMessage?id=2
     * http://192.168.0.107:9060/BussinessA/hystrixMessage?id=3
     */
    @GetMapping("/hystrixMessage")
    public String getMessage(long id) {
        return userService.getMessage(id);
    }

    /**
     * Add User.
     * @return user map.
     */
    @ApiOperation(value = "api to add a user", notes = "The api is used to add a new user into the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Input parameters not meet the requirement"),
            @ApiResponse(code = 500, message = "Internal server error, such as DB error or service crashed etc")
    })
    @PostMapping(value = "addUser")
    public void addUser(@Validated @RequestBody AlgorithmConfigVO algoConfigVO) {

        String content = algoConfigVO.getContent();
        logger.info("+saveAlgorithmConfig+ fileName:{}, content:{}", algoConfigVO.getFileName(), content);

        System.out.println(algoConfigVO.getFileName() + "----" + algoConfigVO.getContent());
        FileUtil.saveFile(algoConfigVO.getContent(), "/Users/user/xxl/log/test.ini");
    }

    @ApiOperation(value = "api to edit algorithm config file", notes = "Front-end page send a request to edit algorithm config file.")
    @PutMapping("/algoConfig")
    public void saveAlgorithmConfig(@Validated @RequestBody AlgorithmConfigVO algoConfigVO) {
        String content = algoConfigVO.getContent();
        logger.info("+saveAlgorithmConfig+ fileName:{}, content:{}", algoConfigVO.getFileName(), content);
        System.out.println(algoConfigVO.getFileName() + "----" + algoConfigVO.getContent());
        FileUtil.saveFile(algoConfigVO.getContent(), "/Users/user/xxl/log/test.ini");
    }
}
