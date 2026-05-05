package com.utp.backwebintegrado.doctor.infrastructure.mapper;

import com.utp.backwebintegrado.doctor.application.dto.SpecialtyRequest;
import com.utp.backwebintegrado.doctor.application.dto.SpecialtyResponse;
import com.utp.backwebintegrado.doctor.domain.Specialty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SpecialtyMapper {
    @Mapping(target = "id", ignore = true)
    Specialty toEntity(SpecialtyRequest request);

    SpecialtyResponse toResponse(Specialty specialty);
}
