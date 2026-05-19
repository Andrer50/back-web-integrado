package com.utp.backwebintegrado.patient.infrastructure.mapper;

import com.utp.backwebintegrado.patient.application.dto.AllergyResponse;
import com.utp.backwebintegrado.patient.domain.Allergy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AllergyMapper {
    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "type", source = "allergen")
    @Mapping(target = "severity", expression = "java(allergy.getSeverity() != null ? allergy.getSeverity().name() : null)")
    AllergyResponse toResponse(Allergy allergy);
}
