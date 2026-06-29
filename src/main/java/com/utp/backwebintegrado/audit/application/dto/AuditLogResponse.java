package com.utp.backwebintegrado.audit.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AuditLogResponse {
    private UUID id;
    private String module;
    private String entityType;
    private UUID entityId;
    private String action;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private String actorEmail;
    private String actorRoles;
    private LocalDateTime changedAt;
    private String description;
}
