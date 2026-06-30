package com.utp.backwebintegrado.audit.infrastructure;

import com.utp.backwebintegrado.audit.domain.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface AuditLogJpaRepository extends JpaRepository<AuditLog, UUID> {
    @Query("SELECT a FROM AuditLog a WHERE " +
            "(:module IS NULL OR a.module = :module) AND " +
            "(:entityType IS NULL OR a.entityType = :entityType) AND " +
            "(:entityId IS NULL OR a.entityId = :entityId) " +
            "ORDER BY a.changedAt DESC")
    Page<AuditLog> search(
            @Param("module") String module,
            @Param("entityType") String entityType,
            @Param("entityId") UUID entityId,
            Pageable pageable
    );
}
