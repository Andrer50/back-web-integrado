package com.utp.backwebintegrado.clinical.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "medications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Medication {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String brand;
    
    private String concentration;
    
    @Column(name = "pharmaceutical_form")
    private String pharmaceuticalForm; // e.g., Tablet, Syrup

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
    }
}
