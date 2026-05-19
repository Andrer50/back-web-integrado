package com.utp.backwebintegrado.patient.application.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AllergyRequest {
    private UUID patientId;
    private String type;
    private String severity;
}
