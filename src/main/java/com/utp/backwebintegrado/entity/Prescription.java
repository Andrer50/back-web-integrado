package com.utp.backwebintegrado.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "prescriptions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // Una receta pertenece a una cita específica
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", unique = true)
    private Appointment appointment;

    @Column(name = "issue_date", nullable = false)
    private LocalDateTime issueDate;

    @Column(name = "general_notes", columnDefinition = "TEXT")
    private String generalNotes;

    // Opcional: Relación bidireccional si necesitas traer los detalles junto con la receta
    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrescriptionDetail> details;
    @PrePersist
    public void generateId() {
        if (this.id == null) {
            // getTimeOrderedEpoch() es el método exacto para UUID v7
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
    }
}
