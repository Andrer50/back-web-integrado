package com.utp.backwebintegrado.lab.infrastructure;

import com.utp.backwebintegrado.lab.application.dto.LabOrderResponse;
import com.utp.backwebintegrado.lab.domain.LabOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LabMapper {
    @Mapping(target = "status", expression = "java(labOrder.getStatus() != null ? labOrder.getStatus().name() : null)")
    @Mapping(target = "resultDetails", source = "labResult.details")
    @Mapping(target = "resultRecordedAt", source = "labResult.recordedAt")
    LabOrderResponse toResponse(LabOrder labOrder);
}
