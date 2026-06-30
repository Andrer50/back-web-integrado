package com.utp.backwebintegrado.consultation.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionResponse {
    private UUID id;
    private String notes;
    private LocalDateTime issueDate;
    private List<PrescriptionItemResponse> items;
    private String doctorFirstName;
    private String doctorLastName;
    private String doctorSpecialty;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
}
