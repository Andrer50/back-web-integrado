package com.utp.backwebintegrado.appointment.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequest {
    private UUID patientId;
    private UUID doctorId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String reason;
}
