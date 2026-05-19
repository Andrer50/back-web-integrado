package com.utp.backwebintegrado.clinical.infrastructure.mapper;

import com.utp.backwebintegrado.clinical.application.dto.MedicationResponse;
import com.utp.backwebintegrado.clinical.domain.Medication;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MedicationMapper {
    MedicationResponse toResponse(Medication medication);
}
