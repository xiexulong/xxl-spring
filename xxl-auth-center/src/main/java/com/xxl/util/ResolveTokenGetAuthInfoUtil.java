package com.xxl.util;

import com.xxl.entity.AuthenticationInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.commons.io.IOUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;
import java.util.List;

@Component
public class ResolveTokenGetAuthInfoUtil {

    private  final Logger logger = LoggerFactory.getLogger(ResolveTokenGetAuthInfoUtil.class);


    @Value("${auth.jwt.header.name}")
    private String headerName;

    @Value("${auth.jwt.header.prefix}")
    private String headerPrefix;

    @Value("${auth.jwt.cert.public}")
    private String publicKeyPath;

    @Value("${auth.jwt.algorithm.type}")
    private String algorithmType;

    @Value("${auth.jwt.cookie.name}")
    private String cookieName;

    public AuthenticationInfo resolveTokenGetAuthInfoUtil(HttpServletRequest request) {

        AuthenticationInfo authenticationInfo = new AuthenticationInfo();
        //Get request header,just for debug.
        printRequestHeader(request);

        // Get token value from header
        resolveToken(request, authenticationInfo);

        // Verify given token and by the way full-in username and roles
        verifyToken(authenticationInfo);

        return authenticationInfo;
    }

    /**
     * this method just for debug.
     * @param request this is a http request.
     */
    private  void printRequestHeader(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String header = headers.nextElement();
            String value = request.getHeader(header);
            logger.info("header: {}, value: {}", header, value);
        }
    }

    private  void resolveToken(HttpServletRequest request, AuthenticationInfo authenticationInfo) {
        // get token in header
        String tokenHeader = request.getHeader(headerName);
        if (tokenHeader != null && tokenHeader.startsWith(headerPrefix + " ")) {
            authenticationInfo.token = tokenHeader.replace(headerPrefix + " ", "");
        }

        // get token in cookie
        if (authenticationInfo.token == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(cookieName)) {
                        authenticationInfo.token = cookie.getValue();
                    }
                }
            }
        }

    }


    private  void verifyToken(AuthenticationInfo authenticationInfo) {
        String token = authenticationInfo.token;
        if (TextUtils.isEmpty(token)) {
            logger.error("Token is null or empty");
            return;
        }

        try (InputStream inputStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(publicKeyPath)) {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(IOUtils.toByteArray(inputStream));
            PublicKey publicKey = KeyFactory.getInstance(algorithmType).generatePublic(spec);

            Claims claims = Jwts.parser()
                    .setSigningKey(publicKey)
                    .parseClaimsJws(token)
                    .getBody();
            authenticationInfo.username = claims.getSubject();
            authenticationInfo.roles = claims.get("roles", List.class);
            logger.info("username: {}, roles: {}", authenticationInfo.username, authenticationInfo.roles);
        } catch (Exception e) {
            logger.error("Jwt token auth failed.", e);
        }
    }
}
