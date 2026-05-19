package com.utp.backwebintegrado.doctor.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.utp.backwebintegrado.clinical.domain.ConsultingRoom;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "doctor_schedules")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorSchedule {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "day_of_week", nullable = false)
    private String dayOfWeek; // MONDAY, TUESDAY, etc.

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "consulting_room_id", nullable = false)
    private ConsultingRoom consultingRoom;

    @Column(name = "slot_duration_minutes", nullable = false)
    private int slotDurationMinutes;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
    }
}

