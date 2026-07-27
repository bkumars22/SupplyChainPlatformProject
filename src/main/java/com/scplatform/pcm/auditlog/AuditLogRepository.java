/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.auditlog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findTop100ByOrderByLoggedAtDesc();

    List<AuditLog> findByEntityTypeAndEntityId(String entityType, String entityId);

    List<AuditLog> findByPerformedByOrderByLoggedAtDesc(String performedBy);
}
