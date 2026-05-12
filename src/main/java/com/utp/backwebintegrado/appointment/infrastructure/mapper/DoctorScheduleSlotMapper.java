package com.utp.backwebintegrado.appointment.infrastructure.mapper;

import com.utp.backwebintegrado.appointment.application.dto.DoctorScheduleSlotResponse;
import com.utp.backwebintegrado.appointment.domain.DoctorScheduleSlot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DoctorScheduleSlotMapper {
    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(target = "doctorName", expression = "java(slot.getDoctor().getFirstName() + \" \" + slot.getDoctor().getLastName())")
    @Mapping(target = "consultingRoomId", source = "consultingRoom.id")
    @Mapping(target = "roomNumber", source = "consultingRoom.roomNumber")
    @Mapping(target = "branchName", source = "consultingRoom.branch.name")
    @Mapping(target = "branchAddress", source = "consultingRoom.branch.address")
    @Mapping(target = "status", expression = "java(slot.getStatus() != null ? slot.getStatus().name() : null)")
    DoctorScheduleSlotResponse toResponse(DoctorScheduleSlot slot);
}
