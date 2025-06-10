package com.example.backservicereportreader.AOP;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ReaderLoggingAspect {
    @Before("execution(* com.example.backservicereportreader.service.FileStorageService.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("📥 [LOG] Iniciando execução de: " + joinPoint.getSignature().getName());
    }

    @AfterReturning(pointcut = "execution(* com.example.backservicereportreader.service.FileStorageService.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        System.out.println("✅ [LOG] Execução bem-sucedida de: " + joinPoint.getSignature().getName());
    }

    @AfterThrowing(pointcut = "execution(* com.example.backservicereportreader.service.FileStorageService.*(..))", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        System.err.println("❌ [LOG] Exceção em " + joinPoint.getSignature().getName() + ": " + ex.getMessage());
    }
}
