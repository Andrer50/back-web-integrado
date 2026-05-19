package com.utp.backwebintegrado.doctor.infrastructure;

import com.utp.backwebintegrado.doctor.domain.DoctorOffDay;
import com.utp.backwebintegrado.doctor.domain.DoctorOffDayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DoctorOffDayRepositoryImpl implements DoctorOffDayRepository {

    private final DoctorOffDayJpaRepository jpaRepository;

    @Override
    public DoctorOffDay save(DoctorOffDay doctorOffDay) {
        return jpaRepository.save(doctorOffDay);
    }

    @Override
    public Optional<DoctorOffDay> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<DoctorOffDay> findByDoctorId(UUID doctorId) {
        return jpaRepository.findByDoctorId(doctorId);
    }

    @Override
    public List<DoctorOffDay> findByDoctorIdAndOffDateGreaterThanEqual(UUID doctorId, LocalDate date) {
        return jpaRepository.findByDoctorIdAndOffDateGreaterThanEqual(doctorId, date);
    }

    @Override
    public Optional<DoctorOffDay> findByDoctorIdAndOffDate(UUID doctorId, LocalDate offDate) {
        return jpaRepository.findByDoctorIdAndOffDate(doctorId, offDate);
    }

    @Override
    public void delete(DoctorOffDay doctorOffDay) {
        jpaRepository.delete(doctorOffDay);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByDoctorIdAndOffDate(UUID doctorId, LocalDate offDate) {
        return jpaRepository.existsByDoctorIdAndOffDate(doctorId, offDate);
    }
}
