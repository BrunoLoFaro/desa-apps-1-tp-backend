package com.example.desabackend.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        log.info("Controller method called: {}.{}", className, methodName);
        try {
            Object result = joinPoint.proceed();
            log.debug("Controller method {}.{} completed successfully", className, methodName);
            return result;
        } catch (Exception ex) {
            log.error("Controller method {}.{} failed: {}", className, methodName, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Around("@within(org.springframework.stereotype.Service)")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        log.debug("Service method called: {}.{}", className, methodName);
        try {
            Object result = joinPoint.proceed();
            log.debug("Service method {}.{} completed", className, methodName);
            return result;
        } catch (Exception ex) {
            log.warn("Service method {}.{} failed: {}", className, methodName, ex.getMessage());
            throw ex;
        }
    }
}