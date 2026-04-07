package com.utp.backwebintegrado.entity;


import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
    @Table(name = "patients", indexes = @Index(name = "uk_patients_document", columnList = "document_number", unique = true))
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class Patient {
        @Id
        @Column(name = "id", updatable = false, nullable = false)
        private UUID id;

        // Relación 1 a 1 con tu entidad User de Auth

        @OneToOne(optional = false, fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", unique = true)
        private User user;

        @Column(name = "document_number", nullable = false, unique = true)
        private String documentNumber;

        @Column(name = "first_name", nullable = false)
        private String firstName;

        @Column(name = "last_name", nullable = false)
        private String lastName;

        @Column(name = "birth_date", nullable = false)
        private LocalDate birthDate;

        @Column(name = "phone")
        private String phone;

        @Column(name = "medical_history", columnDefinition = "TEXT")
        private String medicalHistory;
    @PrePersist
    public void generateId() {
        if (this.id == null) {
            // getTimeOrderedEpoch() es el método exacto para UUID v7
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
    }
    }



