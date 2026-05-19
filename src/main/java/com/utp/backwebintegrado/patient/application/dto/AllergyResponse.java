package com.utp.backwebintegrado.patient.application.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AllergyResponse {
    private UUID id;
    private UUID patientId;
    private String type;
    private String severity;
}
