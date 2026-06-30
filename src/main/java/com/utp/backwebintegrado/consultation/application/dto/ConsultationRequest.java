package com.utp.backwebintegrado.consultation.application.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ConsultationRequest {
    private UUID appointmentId;
    private String notes;
}
