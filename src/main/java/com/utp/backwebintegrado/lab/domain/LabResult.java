package com.utp.backwebintegrado.lab.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "lab_results")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabResult {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_order_id", unique = true)
    private LabOrder labOrder;

    @Column(columnDefinition = "TEXT")
    private String details;

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
