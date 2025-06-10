package com.example.backservicereportreader.AOP;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ReaderAuditAspect {

    @After("execution(* com.example.backservicereportreader.service.FileStorageService.*(..))")
    public void auditAction(JoinPoint joinPoint) {
        System.out.println("🗑️ [AUDIT] Ação auditada: " + joinPoint.getSignature().toShortString());
    }

}
