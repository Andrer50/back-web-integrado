package com.utp.backwebintegrado.lab.infrastructure;

import com.utp.backwebintegrado.lab.application.dto.LabOrderResponse;
import com.utp.backwebintegrado.lab.application.dto.LabResultResponse;
import com.utp.backwebintegrado.lab.domain.LabOrder;
import com.utp.backwebintegrado.lab.domain.LabResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LabMapper {
    @Mapping(target = "consultationId", source = "consultation.id")
    @Mapping(target = "patientId", source = "consultation.appointment.patient.id")
    @Mapping(target = "patientFirstName", source = "consultation.appointment.patient.firstName")
    @Mapping(target = "patientLastName", source = "consultation.appointment.patient.lastName")
    @Mapping(target = "doctorFirstName", source = "consultation.appointment.doctor.firstName")
    @Mapping(target = "doctorLastName", source = "consultation.appointment.doctor.lastName")
    @Mapping(target = "appointmentDate", source = "consultation.appointment.appointmentDate")
    @Mapping(target = "status", expression = "java(labOrder.getStatus() != null ? labOrder.getStatus().name() : null)")
    @Mapping(target = "resultDetails", source = "labResult.details")
    @Mapping(target = "resultRecordedAt", source = "labResult.recordedAt")
    LabOrderResponse toResponse(LabOrder labOrder);

    @Mapping(target = "labOrderId", source = "labOrder.id")
    LabResultResponse toResultResponse(LabResult labResult);
}
