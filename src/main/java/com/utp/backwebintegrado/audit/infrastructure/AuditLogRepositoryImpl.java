package com.utp.backwebintegrado.audit.infrastructure;

import com.utp.backwebintegrado.audit.domain.AuditLog;
import com.utp.backwebintegrado.audit.domain.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AuditLogRepositoryImpl implements AuditLogRepository {
    private final AuditLogJpaRepository jpaRepository;

    @Override
    public AuditLog save(AuditLog auditLog) {
        return jpaRepository.save(auditLog);
    }

    @Override
    public Page<AuditLog> findAll(String module, String entityType, UUID entityId, Pageable pageable) {
        return jpaRepository.search(module, entityType, entityId, pageable);
    }
}
