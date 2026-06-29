package com.utp.backwebintegrado.audit.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AuditLogRepository {
    AuditLog save(AuditLog auditLog);
    Page<AuditLog> findAll(String module, String entityType, UUID entityId, Pageable pageable);
}
