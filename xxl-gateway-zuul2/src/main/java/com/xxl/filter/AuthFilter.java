package com.xxl.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.xxl.util.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AuthFilter extends ZuulFilter {

    private static Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    /**
     * 读取配置文件中排除不需要授权的URL
     * exclude:
     *   auth:
     *     url: /api/login/userLogin #不需要授权验证的请求地址,可设置多个,使用逗号分隔开,会跳过AuthFilter授权验证
     */
    @Value("${login.path}")
    private String excludeAuthUrl;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public String filterType() {
        //由于授权需要在请求之前调用，所以这里使用前置过滤器
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 2;
    }

    @Override
    public boolean shouldFilter() {
        //路径与配置文件中的相匹配，则执行过滤
        RequestContext ctx = RequestContext.getCurrentContext();
        String requestURI = ctx.getRequest().getRequestURI();
        List<String> excludesUrlList = Arrays.asList(excludeAuthUrl.split(","));
        return !excludesUrlList.contains(requestURI);
    }

    @Override
    public Object run() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest httpServletRequest = requestContext.getRequest();
        String token = httpServletRequest.getHeader("token");
        Claims claims;
        try {
            //解析没有异常则表示token验证通过，如有必要可根据自身需求增加验证逻辑
            claims = jwtUtils.parseJwtToken(token);
            //对请求进行路由
            requestContext.setSendZuulResponse(true);
            //请求头加入userId，传给具体的微服务
            requestContext.addZuulRequestHeader("userId", claims.get("userId").toString());
        } catch (ExpiredJwtException expiredJwtEx) {
            logger.error("token : {} 已过期", token);
            //不对请求进行路由
            requestContext.setSendZuulResponse(false);
            Map<String,String> result = new HashMap();
            result.put("code", "40002");
            result.put("msg", "token已过期");

            requestContext.setResponseBody(parseResult(result));
            HttpServletResponse response = requestContext.getResponse();
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType("application/json;charset=utf-8");
        } catch (Exception ex) {
            logger.error("token : {} 验证失败", token);
            //不对请求进行路由
            requestContext.setSendZuulResponse(false);
            Map<String,String> result = new HashMap();
            result.put("code", "40001");
            result.put("msg", "非法token");
            requestContext.setResponseBody(parseResult(result));
            HttpServletResponse response = requestContext.getResponse();
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType("application/json;charset=utf-8");
        }
        return null;
    }

    private String parseResult(Map<String,String> map) {
        try {
            return new ObjectMapper().writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
