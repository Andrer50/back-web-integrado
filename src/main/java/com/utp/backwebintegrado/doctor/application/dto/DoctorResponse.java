package com.utp.backwebintegrado.doctor.application.dto;

import com.utp.backwebintegrado.user.application.dto.UserResponse;
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
public class DoctorResponse {
    private UUID id;
    private String medicalLicenseNumber;
    private String bio;
    private UserResponse user;
    private Set<SpecialtyResponse> specialties;
}
