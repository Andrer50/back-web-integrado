package com.utp.backwebintegrado.clinical.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.utp.backwebintegrado.shared.enumeration.Status;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "consulting_rooms", uniqueConstraints = {
    // Evita que haya dos consultorios con el mismo número en la misma sede
    @UniqueConstraint(columnNames = {"branch_id", "room_number"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultingRoom {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch; // Sede a la que pertenece

    @Column(name = "room_number", nullable = false)
    private String roomNumber; // Ej: "Consultorio 104" o "C-201"

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
