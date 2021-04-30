package com.xxl.service;

import org.springframework.web.bind.annotation.RequestHeader;

import javax.servlet.http.HttpServletRequest;

public interface AuthService {
    void auth(HttpServletRequest request,
                     @RequestHeader("uri") String uri,
                     @RequestHeader("method") String method);


    void resetResourceMap();
}
