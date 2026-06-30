package com.utp.backwebintegrado.audit.infrastructure.mapper;

import com.utp.backwebintegrado.audit.application.dto.AuditLogResponse;
import com.utp.backwebintegrado.audit.domain.AuditLog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {
    AuditLogResponse toResponse(AuditLog auditLog);
}
