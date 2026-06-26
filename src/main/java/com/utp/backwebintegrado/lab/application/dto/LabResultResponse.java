package com.utp.backwebintegrado.lab.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabResultResponse {
    private UUID id;
    private UUID labOrderId;
    private String details;
    private LocalDateTime recordedAt;
}
