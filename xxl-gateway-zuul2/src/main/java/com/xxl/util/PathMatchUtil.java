package com.xxl.util;

import org.springframework.util.AntPathMatcher;

public class PathMatchUtil {
    private static AntPathMatcher matcher = new AntPathMatcher();

    public static boolean isPathMatch(String pattern, String path) {
        return matcher.match(pattern, path);
    }

    public static void main(String[] args) {

        System.out.println(PathMatchUtil.isPathMatch("/api/login","/api/login"));
        System.out.println(PathMatchUtil.isPathMatch("/api/login","/api/addUser/aaa"));
        System.out.println(PathMatchUtil.isPathMatch("/api/login","/api/login?aaa=11"));
    }
}
