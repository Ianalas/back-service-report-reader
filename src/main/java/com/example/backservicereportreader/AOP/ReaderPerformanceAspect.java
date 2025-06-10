package com.example.backservicereportreader.AOP;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ReaderPerformanceAspect {

    @Around("execution(* com.example.backservicereportreader.service.FileStorageService.*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long duration = System.currentTimeMillis() - start;
        System.out.println("⏱️ [PERFORMANCE] Tempo de execução de " +
                joinPoint.getSignature().getName() + ": " + duration + " ms");

        return result;
    }
}
