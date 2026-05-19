package com.utp.backwebintegrado.clinical.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PrescriptionResponse {
    private UUID id;
    private String notes;
    private LocalDateTime issueDate;
    private List<PrescriptionItemResponse> items;
}
