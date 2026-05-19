package com.utp.backwebintegrado.doctor.infrastructure;

import com.utp.backwebintegrado.doctor.domain.DoctorOffDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DoctorOffDayJpaRepository extends JpaRepository<DoctorOffDay, UUID> {
    List<DoctorOffDay> findByDoctorId(UUID doctorId);
    List<DoctorOffDay> findByDoctorIdAndOffDateGreaterThanEqual(UUID doctorId, LocalDate date);
    Optional<DoctorOffDay> findByDoctorIdAndOffDate(UUID doctorId, LocalDate offDate);
    boolean existsByDoctorIdAndOffDate(UUID doctorId, LocalDate offDate);
}
