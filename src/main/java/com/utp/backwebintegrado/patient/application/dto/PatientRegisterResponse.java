package com.utp.backwebintegrado.patient.application.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientRegisterResponse {
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
