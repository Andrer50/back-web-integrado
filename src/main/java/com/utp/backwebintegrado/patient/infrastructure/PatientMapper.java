package com.utp.backwebintegrado.patient.infrastructure;

import com.utp.backwebintegrado.patient.application.dto.PatientRequest;
import com.utp.backwebintegrado.patient.application.dto.PatientResponse;
import com.utp.backwebintegrado.patient.domain.Patient;
import com.utp.backwebintegrado.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    @Mapping(target = "user", source = "user")
    @Mapping(target = "id", ignore = true) // Lo genera el @PrePersist
    Patient toEntity(PatientRequest request, User user);

    PatientResponse toResponse(Patient patient);
}
