package com.utp.backwebintegrado.appointment.infrastructure;

import com.utp.backwebintegrado.appointment.domain.DoctorScheduleSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DoctorScheduleSlotJpaRepository extends JpaRepository<DoctorScheduleSlot, UUID> {

    @Query("SELECT s FROM DoctorScheduleSlot s " +
           "JOIN s.doctor d " +
           "JOIN d.specialties spec " +
           "JOIN s.consultingRoom r " +
           "JOIN r.branch b " +
           "WHERE spec.id = :specialtyId " +
           "AND (:branchId IS NULL OR b.id = :branchId) " +
           "AND s.slotDate >= :startDate " +
           "AND s.slotDate <= :endDate " +
           "AND s.status = com.utp.backwebintegrado.shared.enumeration.SlotStatus.AVAILABLE " +
           "ORDER BY d.lastName ASC, s.slotDate ASC, s.startTime ASC")
    List<DoctorScheduleSlot> findAvailableSlots(
            @Param("specialtyId") UUID specialtyId,
            @Param("branchId") UUID branchId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
