package com.utp.backwebintegrado.consultation.application.dto;

import lombok.Data;

import java.util.List;

@Data
public class PrescriptionRequest {
    private String notes;
    private List<PrescriptionItemRequest> items;
}
