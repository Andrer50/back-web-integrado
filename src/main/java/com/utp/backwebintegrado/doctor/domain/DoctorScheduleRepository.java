package com.utp.backwebintegrado.doctor.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DoctorScheduleRepository {
    DoctorSchedule save(DoctorSchedule doctorSchedule);
    List<DoctorSchedule> saveAll(List<DoctorSchedule> schedules);
    Optional<DoctorSchedule> findById(UUID id);
    List<DoctorSchedule> findByDoctorId(UUID doctorId);
    List<DoctorSchedule> findByDoctorIdAndIsActive(UUID doctorId, boolean isActive);
    List<DoctorSchedule> findByDayOfWeekAndIsActive(String dayOfWeek, boolean isActive);
    void delete(DoctorSchedule doctorSchedule);
    void deleteById(UUID id);
}
