package com.utp.backwebintegrado.lab.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.utp.backwebintegrado.clinical.domain.Consultation;
import com.utp.backwebintegrado.shared.enumeration.LabOrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "lab_orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabOrder {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_id", nullable = false)
    private Consultation consultation;

    @Column(nullable = false)
    private String type; // e.g., Blood Test, X-Ray

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LabOrderStatus status;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
        if (this.orderedAt == null) {
            this.orderedAt = LocalDateTime.now();
        }
    }
}
