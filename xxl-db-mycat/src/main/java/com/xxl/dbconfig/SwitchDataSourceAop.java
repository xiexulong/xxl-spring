package com.xxl.dbconfig;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(0)//保证aop执行顺序在数据库事务之前
public class SwitchDataSourceAop {

    @Before("execution(* com.xxl.service.*.*(..))")
    public void process(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        if (methodName.startsWith("get")
                || methodName.startsWith("count")
                || methodName.startsWith("find")
                || methodName.startsWith("list")
                || methodName.startsWith("select")
                || methodName.startsWith("check")) {
            DataSourceContextHolder.setDbType("selectDataSource");
        } else {
            DataSourceContextHolder.setDbType("updateDataSource");
        }
    }
}
