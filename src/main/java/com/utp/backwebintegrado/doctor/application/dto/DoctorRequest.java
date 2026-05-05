package com.utp.backwebintegrado.doctor.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String medicalLicenseNumber;
    private String bio;
    private Set<UUID> specialtyIds;
}
