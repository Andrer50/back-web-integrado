package com.utp.backwebintegrado.consultation.application.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ConsultationDiagnosisResponse {
    private UUID id;
    private String icd10;
    private String description;
    private String type;
}
