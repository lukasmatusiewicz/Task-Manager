package com.lucc.taskmanager.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect
{
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.lucc.taskmanager.controller.*.*(..))")
    public void logBefore(JoinPoint joinPoint)
    {
        logger.info("Before: {}", joinPoint.getSignature().toShortString());
    }

    @AfterReturning("execution(* com.lucc.taskmanager.controller.*.*(..))")
    public void logAfterReturning(JoinPoint joinPoint)
    {
        logger.info("After: {}", joinPoint.getSignature().toShortString());
    }
}
