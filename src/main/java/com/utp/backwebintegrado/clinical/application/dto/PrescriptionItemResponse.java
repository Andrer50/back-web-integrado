package com.utp.backwebintegrado.clinical.application.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PrescriptionItemResponse {
    private UUID id;
    private String medicationName;
    private String dosage;
    private String frequency;
    private String duration;
    private String instructions;
}
