package com.utp.backwebintegrado.clinical.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "consultation_vitals")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationVitals {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_id", nullable = false)
    private Consultation consultation;

    private Double weight;
    private Double height;
    
    @Column(name = "blood_pressure")
    private String bloodPressure;
    
    private Double temperature;
    
    @Column(name = "heart_rate")
    private Integer heartRate;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
        if (this.recordedAt == null) {
            this.recordedAt = LocalDateTime.now();
        }
    }
}
