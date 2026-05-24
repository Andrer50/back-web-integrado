package com.utp.backwebintegrado.lab.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabOrderResponse {
    private UUID id;
    private String type;
    private String name;
    private String status;
    private LocalDateTime orderedAt;
    private String resultDetails;
    private LocalDateTime resultRecordedAt;
}
