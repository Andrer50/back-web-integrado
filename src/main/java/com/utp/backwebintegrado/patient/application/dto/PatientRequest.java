package com.utp.backwebintegrado.patient.application.dto;

import com.utp.backwebintegrado.shared.enumeration.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private LocalDate birthDate;
    private String documentNumber;
    private Gender gender;
    private String address;
}
