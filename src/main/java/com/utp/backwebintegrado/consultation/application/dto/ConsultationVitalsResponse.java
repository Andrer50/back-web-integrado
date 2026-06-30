package com.utp.backwebintegrado.consultation.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ConsultationVitalsResponse {
    private UUID id;
    private Double weight;
    private Double height;
    private String bloodPressure;
    private Double temperature;
    private Integer heartRate;
    private LocalDateTime recordedAt;
}
