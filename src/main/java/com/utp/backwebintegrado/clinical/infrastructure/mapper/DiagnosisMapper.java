package com.utp.backwebintegrado.clinical.infrastructure.mapper;

import com.utp.backwebintegrado.clinical.application.dto.DiagnosisResponse;
import com.utp.backwebintegrado.clinical.domain.Diagnosis;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DiagnosisMapper {
    DiagnosisResponse toResponse(Diagnosis diagnosis);
}
