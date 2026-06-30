package com.utp.backwebintegrado.audit.presentation;

import com.utp.backwebintegrado.audit.application.AuditService;
import com.utp.backwebintegrado.audit.application.dto.AuditLogResponse;
import com.utp.backwebintegrado.shared.dto.ApiResponse;
import com.utp.backwebintegrado.shared.utility.ConstantUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
public class AuditController {
    private final AuditService auditService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> findAll(
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) UUID entityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal Jwt jwt) {

        List<String> roles = jwt.getClaimAsStringList("roles");
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(ApiResponse.<Page<AuditLogResponse>>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(auditService.findAll(module, entityType, entityId, pageable, roles))
                .build());
    }
}
