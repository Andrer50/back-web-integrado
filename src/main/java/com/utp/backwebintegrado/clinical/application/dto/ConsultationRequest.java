package com.utp.backwebintegrado.clinical.application.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ConsultationRequest {
    private UUID appointmentId;
    private String notes;
}
