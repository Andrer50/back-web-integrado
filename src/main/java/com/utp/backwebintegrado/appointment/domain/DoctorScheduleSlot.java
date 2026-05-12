package com.utp.backwebintegrado.appointment.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.utp.backwebintegrado.clinical.domain.ConsultingRoom;
import com.utp.backwebintegrado.doctor.domain.Doctor;
import com.utp.backwebintegrado.shared.enumeration.SlotStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "doctor_schedule_slots", uniqueConstraints = {
    // REGLA DE ORO: Un consultorio físico solo puede ser ocupado por un slot de tiempo a la vez en una fecha/hora específicas
    @UniqueConstraint(name = "uk_slots_consulting_room_time", columnNames = {"consulting_room_id", "slot_date", "start_time"}),
    // Un médico no puede tener dos slots al mismo tiempo en el mismo día
    @UniqueConstraint(name = "uk_slots_doctor_time", columnNames = {"doctor_id", "slot_date", "start_time"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorScheduleSlot {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "consulting_room_id", nullable = false)
    private ConsultingRoom consultingRoom;

    @Column(name = "slot_date", nullable = false)
    private LocalDate slotDate; // Ej: 2026-05-14

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime; // Ej: 15:00

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime; // Ej: 15:15

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SlotStatus status; // AVAILABLE, BOOKED, BLOCKED

    @OneToOne(mappedBy = "scheduleSlot", fetch = FetchType.LAZY)
    private Appointment appointment;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
    }
}
