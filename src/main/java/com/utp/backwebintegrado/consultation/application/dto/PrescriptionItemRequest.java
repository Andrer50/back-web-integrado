package com.utp.backwebintegrado.consultation.application.dto;

import lombok.Data;

@Data
public class PrescriptionItemRequest {
    private String medicationName;
    private String dosage;
    private String frequency;
    private String duration;
    private String instructions;
}
