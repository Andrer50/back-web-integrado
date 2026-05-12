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
public class AppointmentResponse {
    private UUID id;
    private UUID patientId;
    private String patientFirstName;
    private String patientLastName;
    private UUID doctorId;
    private String doctorFirstName;
    private String doctorLastName;
    private String doctorSpecialty;
    private String doctorMedicalLicenseNumber;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String status;
    private String reason;
}
