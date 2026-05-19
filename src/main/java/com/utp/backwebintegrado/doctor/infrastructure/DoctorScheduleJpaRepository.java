package com.utp.backwebintegrado.doctor.infrastructure;

import com.utp.backwebintegrado.doctor.domain.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DoctorScheduleJpaRepository extends JpaRepository<DoctorSchedule, UUID> {
    List<DoctorSchedule> findByDoctorId(UUID doctorId);
    List<DoctorSchedule> findByDoctorIdAndIsActive(UUID doctorId, boolean isActive);
    List<DoctorSchedule> findByDayOfWeekAndIsActive(String dayOfWeek, boolean isActive);
}
