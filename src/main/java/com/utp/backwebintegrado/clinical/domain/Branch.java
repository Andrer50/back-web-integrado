package com.utp.backwebintegrado.clinical.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.utp.backwebintegrado.shared.enumeration.Status;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "branches")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Branch {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name; // Ej: "Sede Los Olivos"

    @Column(nullable = false)
    private String address; // Ej: "Av. Alfredo Mendiola N 6301, Los Olivos"

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status; // ACTIVE, INACTIVE

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
    }
}
