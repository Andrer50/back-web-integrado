package com.utp.backwebintegrado.doctor.application.dto;

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
public class DoctorOffDayRequest {
    private UUID id;
    private LocalDate offDate;
    private String reason;
}
