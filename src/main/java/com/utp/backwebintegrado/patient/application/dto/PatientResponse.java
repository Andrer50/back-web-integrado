package com.utp.backwebintegrado.patient.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class PatientResponse {
    private UUID id;
    private String email;
    private String password;
    private String phone;
    private LocalDate birthDate;
    private String documentNumber;
    private String firstName;
    private String lastName;
    private String createdAt;
    private String updatedAt;
}
