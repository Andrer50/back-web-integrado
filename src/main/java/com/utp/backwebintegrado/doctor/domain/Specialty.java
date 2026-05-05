package com.utp.backwebintegrado.doctor.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.utp.backwebintegrado.shared.enumeration.Status;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "specialties")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Specialty {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
        if (this.status == null) {
            this.status = Status.ACTIVE;
        }
    }
}
