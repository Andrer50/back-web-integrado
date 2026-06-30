package com.utp.backwebintegrado.consultation.application.dto;

import lombok.Data;

@Data
public class ConsultationDiagnosisRequest {
    private String icd10;
    private String description;
    private String type; // PRINCIPAL, SECUNDARIO
}
