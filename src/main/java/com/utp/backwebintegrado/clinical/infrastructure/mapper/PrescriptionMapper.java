package com.utp.backwebintegrado.clinical.infrastructure.mapper;

import com.utp.backwebintegrado.clinical.application.dto.PrescriptionItemResponse;
import com.utp.backwebintegrado.clinical.application.dto.PrescriptionResponse;
import com.utp.backwebintegrado.clinical.domain.Prescription;
import com.utp.backwebintegrado.clinical.domain.PrescriptionItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PrescriptionMapper {
    PrescriptionResponse toResponse(Prescription prescription);

    @Mapping(target = "medicationName", source = "medication.name")
    PrescriptionItemResponse toItemResponse(PrescriptionItem item);
}
