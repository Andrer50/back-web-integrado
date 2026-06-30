package com.utp.backwebintegrado.lab.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabOrderResponse {
    private UUID id;
    private UUID consultationId;
    private UUID patientId;
    private String patientFirstName;
    private String patientLastName;
    private String doctorFirstName;
    private String doctorLastName;
    private LocalDate appointmentDate;
    private String type;
    private String name;
    private String status;
    private LocalDateTime orderedAt;
    private String resultDetails;
    private LocalDateTime resultRecordedAt;
}
