package com.utp.backwebintegrado.appointment.infrastructure;

import com.utp.backwebintegrado.appointment.domain.DoctorScheduleSlot;
import com.utp.backwebintegrado.appointment.domain.DoctorScheduleSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DoctorScheduleSlotRepositoryImpl implements DoctorScheduleSlotRepository {

    private final DoctorScheduleSlotJpaRepository jpaRepository;

    @Override
    public DoctorScheduleSlot save(DoctorScheduleSlot slot) {
        return jpaRepository.save(slot);
    }

    @Override
    public List<DoctorScheduleSlot> saveAll(List<DoctorScheduleSlot> slots) {
        return jpaRepository.saveAll(slots);
    }

    @Override
    public Optional<DoctorScheduleSlot> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<DoctorScheduleSlot> findAvailableSlots(UUID specialtyId, UUID branchId, LocalDate startDate, LocalDate endDate) {
        return jpaRepository.findAvailableSlots(specialtyId, branchId, startDate, endDate);
    }

    @Override
    public List<DoctorScheduleSlot> findByDoctorIdAndStartDate(UUID doctorId, LocalDate startDate) {
        return jpaRepository.findByDoctorIdAndStartDate(doctorId, startDate);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
