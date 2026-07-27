/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.auditlog;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class AuditAspect {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Around("@annotation(auditable)")
    public Object auditMethod(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        String performedBy = resolveUsername();
        Throwable caughtException = null;
        Object result = null;

        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            caughtException = ex;
        }

        try {
            AuditLog log = new AuditLog();
            log.setEntityType(auditable.entityType());
            log.setEntityId(extractEntityId(joinPoint));
            log.setAction(auditable.action());
            log.setPerformedBy(performedBy);
            log.setDetails(caughtException != null ? "FAILED: " + caughtException.getMessage() : "SUCCESS");
            log.setLoggedAt(LocalDateTime.now());
            auditLogRepository.save(log);
        } catch (Exception loggingException) {
            // Never let audit logging break the main flow
        }

        if (caughtException != null) {
            throw caughtException;
        }

        return result;
    }

    private String resolveUsername() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && auth.getName() != null) {
                return auth.getName();
            }
        } catch (Exception ignored) {
            // Security context may not be available in all contexts
        }
        return "system";
    }

    private String extractEntityId(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0 && args[0] != null) {
            return args[0].toString();
        }
        return "";
    }
}
