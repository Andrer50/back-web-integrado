package com.utp.backwebintegrado.consultation.application.dto;

import lombok.Data;

@Data
public class ConsultationVitalsRequest {
    private Double weight;
    private Double height;
    private String bloodPressure;
    private Double temperature;
    private Integer heartRate;
}
