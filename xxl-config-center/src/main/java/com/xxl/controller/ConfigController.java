package com.xxl.controller;

import com.xxl.model.Config;
import com.xxl.model.vo.ConfigListVO;
import com.xxl.model.vo.ConfigUpdateVO;
import com.xxl.service.ConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


@Api(tags = "Config")
@Validated
@RestController
public class ConfigController {
    private static final Logger logger = LoggerFactory.getLogger(ConfigController.class);

//    @Value("${xxl.bus.value}")
//    private String busValue;


    @Autowired
    private Environment env;

    private final ConfigService configService;

    
    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }
    


    /**
     * find configs.
     * @param application belong to application name.
     * @param item String.
     * @param favorite ,0-not favorite, 1-favorite, default 0.
     * @return ConfigListVO {@ConfigListVO}.
     */
    @ApiOperation(value = "query config list")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "query successful"),
            @ApiResponse(code = 500, message = "Internal error"),
    })
    @GetMapping("/configs")
    public ConfigListVO list(@ApiParam(value = "application field")
                                 @RequestParam(value = "application", required = false) String application,
                             @ApiParam(value = "item field")
                             @RequestParam(value = "item", required = false) String item,
                             @ApiParam(value = "favorite field")
                             @RequestParam(value = "favorite", required = false) Integer favorite) {
//        busValue = env.getProperty("xxl.bus.value");
//        logger.info("==================={}", busValue);
        List<Config> params = configService.list(application, item, favorite);
        List<ConfigListVO.Configuration> configs = params.stream()
                .map(c -> new ConfigListVO.Configuration(c.getId(), c.getApplication(), c.getProfile(),
                        c.getLabel(), c.getItem(), c.getValue(), c.getRemark(), c.getType().getIntValue(),
                        c.getFavorite()))
                .collect(Collectors.toList());
        ConfigListVO configListVO = new ConfigListVO();
        configListVO.setConfigs(configs);
        return configListVO;
    }

    /**
     * update the configs.
     * @param vo {@link ConfigUpdateVO}.
     */
    @ApiOperation(value = "Update config")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "update successful"),
            @ApiResponse(code = 400, message = "bad request, such as illegal argument or method argument not valid"),
            @ApiResponse(code = 417, message = "expectation failed, such as update rows number is incorrect"),
            @ApiResponse(code = 500, message = "not found, such as empty result data"),
    })
    @PutMapping("/configs")
    public void update(@Valid @RequestBody ConfigUpdateVO vo) {
        configService.update(vo.getConfigMap());
    }



}
