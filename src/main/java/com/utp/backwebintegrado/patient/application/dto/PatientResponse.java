package com.utp.backwebintegrado.patient.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponse {
    private UUID id;
    private String email;
    private String status;
    private String phone;
    private LocalDate birthDate;
    private String documentNumber;
    private String firstName;
    private String lastName;
    private String gender;
    private String address;
}
