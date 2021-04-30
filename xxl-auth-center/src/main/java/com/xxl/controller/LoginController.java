package com.xxl.controller;


import com.xxl.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Api(tags = "Default")
@RestController
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private AuthService authService;

    /**
     * 网关xxl-gateway-zuul 是nginx拦截再转发给AuthCenter 去认证
     * 网关xxl-gateway-zuul2 是自定义拦截器认证在转发
     * 用于测试网关xxl-gateway-zuul2拦截检测密码后再转发
     * http://192.168.0.108:9920/AuthCenter/login?username=xxl&password=password 登陆再转发
     * @return
     */
    @GetMapping("/login")
    public String getHello() {
        logger.info("call business-a getHello(), response hello xxl");
        return "hello xxl!";
    }

    @ApiOperation(value = "api to do authentication",
            notes = "Authorize the user. A valid Token is required and two headers are essential, "
                    + "including 'method' and 'uri'. Token exists in the header. Generally "
                    + "speaking, it is in the cookie of the header. The form is similar to header: "
                    + "cookie, value: JTOKEN=XXX.")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access is denied"),
            @ApiResponse(code = 500, message = "Internal server error, such as DB error or "
                    + "service crashed etc")
    })
    @GetMapping("${auth.jwt.url.auth}")
    public void auth(HttpServletRequest request,
                     @ApiParam(value = "This header contains the method, e.g. the header 'method:get' "
                             + "holds the method 'GET'", required = true)
                     @RequestHeader("uri") String uri,
                     @ApiParam(value = "This header contains the url, e.g. the header "
                             + "'uri:/operations/rolemanagement.html' "
                             + "holds the uri '/operations/rolemanagement.html'",
                             required = true)
                     @RequestHeader("method") String method) {
        logger.info("BaseRestController auth, "
                +  "Enter BaseRestController uri:  " + uri
                + "Enter BaseRestController method:  " + method);
        authService.auth(request, uri, method);
    }

    /**
     * 登录接口 这里登陆接口使用的是filter LoginFilter 是在通过访问 /login 的POST请求是被首先被触发的过滤器，默认实现是 UsernamePasswordAuthenticationFilter
     * 也可以像下面一样显式 自定义登陆接口， 如果使用此登录控制器触发登录认证，需要禁用登录认证过滤器
     */
   /* @PostMapping(value = "/login")
    public HttpResult login(@RequestBody LoginBean loginBean, HttpServletRequest request) throws IOException {
        String username = loginBean.getUsername();
        String password = loginBean.getPassword();

        // 系统登录认证
        JwtAuthenticatioToken token = SecurityUtils.login(request, username, password, authenticationManager);

        return HttpResult.ok(token);
    }*/
}
