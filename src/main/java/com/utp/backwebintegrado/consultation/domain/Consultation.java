package com.utp.backwebintegrado.consultation.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.utp.backwebintegrado.appointment.domain.Appointment;
import com.utp.backwebintegrado.shared.enumeration.ConsultationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "consultations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Consultation {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", unique = true)
    private Appointment appointment;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ConsultationStatus status;

    public ConsultationStatus getStatus() {
        return this.status != null ? this.status : ConsultationStatus.PENDING;
    }

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = ConsultationStatus.PENDING;
        }
    }
}
