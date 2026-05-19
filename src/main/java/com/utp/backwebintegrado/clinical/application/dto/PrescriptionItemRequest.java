package com.utp.backwebintegrado.clinical.application.dto;

import lombok.Data;

@Data
public class PrescriptionItemRequest {
    private String medicationName;
    private String dosage;
    private String frequency;
    private String duration;
    private String instructions;
}
