package com.utp.backwebintegrado.doctor.infrastructure;

import com.utp.backwebintegrado.doctor.domain.DoctorSchedule;
import com.utp.backwebintegrado.doctor.domain.DoctorScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DoctorScheduleRepositoryImpl implements DoctorScheduleRepository {

    private final DoctorScheduleJpaRepository jpaRepository;

    @Override
    public DoctorSchedule save(DoctorSchedule doctorSchedule) {
        return jpaRepository.save(doctorSchedule);
    }

    @Override
    public List<DoctorSchedule> saveAll(List<DoctorSchedule> schedules) {
        return jpaRepository.saveAll(schedules);
    }

    @Override
    public Optional<DoctorSchedule> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<DoctorSchedule> findByDoctorId(UUID doctorId) {
        return jpaRepository.findByDoctorId(doctorId);
    }

    @Override
    public List<DoctorSchedule> findByDoctorIdAndIsActive(UUID doctorId, boolean isActive) {
        return jpaRepository.findByDoctorIdAndIsActive(doctorId, isActive);
    }

    @Override
    public List<DoctorSchedule> findByDayOfWeekAndIsActive(String dayOfWeek, boolean isActive) {
        return jpaRepository.findByDayOfWeekAndIsActive(dayOfWeek, isActive);
    }

    @Override
    public void delete(DoctorSchedule doctorSchedule) {
        jpaRepository.delete(doctorSchedule);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
