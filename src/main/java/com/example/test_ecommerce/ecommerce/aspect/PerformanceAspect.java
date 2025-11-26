package com.example.test_ecommerce.ecommerce.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceAspect {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceAspect.class);

    @Around("execution(* com.example.test_ecommerce.ecommerce.services.*.*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        logger.info("Method {} executed in {} ms",
                joinPoint.getSignature().getName(),
                executionTime);

        // Alert if method takes too long
        if (executionTime > 1000) {
            logger.warn("SLOW QUERY: Method {} took {} ms",
                    joinPoint.getSignature().getName(),
                    executionTime);
        }

        return result;
    }
}