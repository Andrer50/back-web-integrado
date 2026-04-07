package com.utp.backwebintegrado.mapper;

import com.utp.backwebintegrado.dto.response.PatientRegisterResponse;
import com.utp.backwebintegrado.entity.Patient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    PatientRegisterResponse toResponse (Patient entity);
}
