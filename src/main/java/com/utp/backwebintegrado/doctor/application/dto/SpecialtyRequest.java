package com.utp.backwebintegrado.doctor.application.dto;

import com.utp.backwebintegrado.shared.enumeration.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialtyRequest {
    private String name;
    private String description;
    private Status status;
}
