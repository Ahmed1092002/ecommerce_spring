package com.example.test_ecommerce.ecommerce.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Log all service method calls
    @Before("execution(* com.example.test_ecommerce.ecommerce.services.*.*(..))")
    public void logBeforeMethod(JoinPoint joinPoint) {
        logger.info("Method called: {} with arguments: {}",
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    // Log successful method returns
    @AfterReturning(pointcut = "execution(* com.example.test_ecommerce.ecommerce.services.*.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("Method {} executed successfully. Result: {}",
                joinPoint.getSignature().getName(),
                result);
    }

    // Log exceptions
    @AfterThrowing(pointcut = "execution(* com.example.test_ecommerce.ecommerce.services.*.*(..))", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        logger.error("Exception in method {}: {}",
                joinPoint.getSignature().getName(),
                exception.getMessage(),
                exception);
    }
}