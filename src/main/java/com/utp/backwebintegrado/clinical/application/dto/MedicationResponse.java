package com.utp.backwebintegrado.clinical.application.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class MedicationResponse {
    private UUID id;
    private String name;
    private String brand;
    private String concentration;
    private String pharmaceuticalForm;
}
