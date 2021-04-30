package com.xxl.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.xxl.util.JwtUtils;
import com.xxl.util.PathMatchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoginFilter extends ZuulFilter {

    @Value("${login.path}")
    private String loginUrl;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    /**
     * common:
     *   login:
     *     url: /login  #登录请求地址,可设置多个,使用逗号分隔开
     * @return
     */
    @Override
    public boolean shouldFilter() {
        //只有路径与配置文件中配置的登录路径相匹配，才会放行该过滤器,执行过滤操作
        RequestContext ctx = RequestContext.getCurrentContext();
        String requestURI = ctx.getRequest().getRequestURI();
        for (String url : loginUrl.split(",")) {
            if (PathMatchUtil.isPathMatch(url, requestURI)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object run() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        try {
            HttpServletRequest httpServletRequest = requestContext.getRequest();
            //此处简单模拟登录,并非生产环境登录使用.
            String username = httpServletRequest.getParameter("username");
            String password = httpServletRequest.getParameter("password");
            if ("xxl".equals(username) && "password".equals(password)) {
                //表示登录成功,服务器端需要生成token返回给客户端
                Map<String, Object> claimsMap = new HashMap<>();
                claimsMap.put("username", "username");
                claimsMap.put("userId", "helloxxl");
                String jwtToken = jwtUtils.createJwtToken(claimsMap);
                //响应头设置token
                requestContext.addZuulResponseHeader("token", jwtToken);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
