package com.utp.backwebintegrado.doctor.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DoctorOffDayRepository {
    DoctorOffDay save(DoctorOffDay doctorOffDay);
    Optional<DoctorOffDay> findById(UUID id);
    List<DoctorOffDay> findByDoctorId(UUID doctorId);
    List<DoctorOffDay> findByDoctorIdAndOffDateGreaterThanEqual(UUID doctorId, LocalDate date);
    Optional<DoctorOffDay> findByDoctorIdAndOffDate(UUID doctorId, LocalDate offDate);
    void delete(DoctorOffDay doctorOffDay);
    void deleteById(UUID id);
    boolean existsByDoctorIdAndOffDate(UUID doctorId, LocalDate offDate);
}
