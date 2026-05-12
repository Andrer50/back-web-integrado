package com.utp.backwebintegrado.appointment.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DoctorScheduleSlotRepository {
    DoctorScheduleSlot save(DoctorScheduleSlot slot);
    List<DoctorScheduleSlot> saveAll(List<DoctorScheduleSlot> slots);
    Optional<DoctorScheduleSlot> findById(UUID id);
    List<DoctorScheduleSlot> findAvailableSlots(UUID specialtyId, UUID branchId, LocalDate startDate, LocalDate endDate);
    List<DoctorScheduleSlot> findByDoctorIdAndStartDate(UUID doctorId, LocalDate startDate);
    void deleteById(UUID id);
}
