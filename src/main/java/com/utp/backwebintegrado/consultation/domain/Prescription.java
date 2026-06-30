package com.utp.backwebintegrado.consultation.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.utp.backwebintegrado.consultation.domain.Consultation;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "prescriptions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_id", unique = true)
    private Consultation consultation;

    @Column(name = "issue_date", nullable = false)
    private LocalDateTime issueDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrescriptionItem> items;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
        if (this.issueDate == null) {
            this.issueDate = LocalDateTime.now();
        }
    }
}
