package com.xxl.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicLong;

@Aspect
@Component
@ConditionalOnProperty(prefix = "global.monitoring", name = "enabled", havingValue = "true")
public class AuthRequestMetricsResolver {
    private static final Logger logger = LoggerFactory.getLogger(AuthRequestMetricsResolver.class);

    private static final String METRICS_AUTH_TOTAL_COUNT = "auth_total_count";
    private static final String METRICS_AUTH_PASS_COUNT = "auth_pass_count";
    private static final String METRICS_AUTH_REJECT_COUNT = "auth_reject_count";
    private AtomicLong authTotalCountGauge;
    private AtomicLong authPassCountGauge;
    private AtomicLong authRejectCountGauge;

    private final MeterRegistry registry;

    @Autowired
    public AuthRequestMetricsResolver(MeterRegistry registry) {
        this.registry = registry;
    }

    @PostConstruct
    private void init() {
        logger.info("AuthRequestMetricsResolver init");
        registerMetrics();
    }

    private void registerMetrics() {
        authTotalCountGauge = registry.gauge(METRICS_AUTH_TOTAL_COUNT, new AtomicLong(0));
        authPassCountGauge = registry.gauge(METRICS_AUTH_PASS_COUNT, new AtomicLong(0));
        authRejectCountGauge = registry.gauge(METRICS_AUTH_REJECT_COUNT, new AtomicLong(0));
    }

    @Pointcut("execution(** com.xxl.service.AuthService.auth(..))")
    public void auth() {
    }

    @Before("auth()")
    public void before() {
        authTotalCountGauge.addAndGet(1);
    }

    @AfterReturning("auth()")
    public void afterReturning() {
        authPassCountGauge.addAndGet(1);
    }

    @AfterThrowing("auth()")
    public void afterThrowing() {
        authRejectCountGauge.addAndGet(1);
    }
}
