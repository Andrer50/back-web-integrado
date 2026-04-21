package com.utp.backwebintegrado.patient.infrastructure;

import com.utp.backwebintegrado.patient.application.dto.PatientRegisterRequest;
import com.utp.backwebintegrado.patient.application.dto.PatientRegisterResponse;
import com.utp.backwebintegrado.patient.domain.Patient;
import com.utp.backwebintegrado.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    @Mapping(target = "user", source = "user")
    @Mapping(target = "id", ignore = true) // Lo genera el @PrePersist
    Patient toEntity(PatientRegisterRequest request, User user);

    PatientRegisterResponse toResponse(Patient patient);
}
