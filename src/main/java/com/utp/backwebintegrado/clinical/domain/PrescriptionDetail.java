package com.utp.backwebintegrado.clinical.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "prescription_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionDetail {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    @Column(name = "medication_name", nullable = false)
    private String medicationName;

    @Column(nullable = false)
    private String dosage; // Ej: "500mg"

    @Column(nullable = false)
    private String frequency; // Ej: "Cada 8 horas"

    @Column(nullable = false)
    private String duration; // Ej: "Por 5 días"
    @PrePersist
    public void generateId() {
        if (this.id == null) {
            // getTimeOrderedEpoch() es el método exacto para UUID v7
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
    }
}