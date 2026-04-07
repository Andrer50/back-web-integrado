package com.utp.backwebintegrado.entity;


import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "doctors", indexes = @Index(name = "uk_doctors_license", columnList = "medical_license_number", unique = true))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // Relación 1 a 1: Un registro de médico le pertenece a un único Usuario
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(nullable = false)
    private String specialty; // Ej: Pediatría, Cardiología

    // Número de colegiatura médica (ej. CMP)
    @Column(name = "medical_license_number", nullable = false, unique = true)
    private String medicalLicenseNumber;
    @PrePersist
    public void generateId() {
        if (this.id == null) {
            // getTimeOrderedEpoch() es el método exacto para UUID v7
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
    }
}