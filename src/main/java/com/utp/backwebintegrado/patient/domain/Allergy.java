package com.utp.backwebintegrado.patient.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.utp.backwebintegrado.shared.enumeration.AllergySeverity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "allergies")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Allergy {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false)
    private String allergen;

    @Column(nullable = false)
    private String type; // e.g., Food, Medication, Environmental

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AllergySeverity severity;

    @Column(columnDefinition = "TEXT")
    private String reaction;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
    }
}
