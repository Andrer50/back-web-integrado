package com.utp.backwebintegrado.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    // Puedes cambiar el tipo String por tu Enum UserRole si lo tienes
    // importado en este microservicio (ej. @Enumerated(EnumType.STRING)).
    @Column(nullable = false, length = 20)
    private String role;

    // Opcional: Para saber si el usuario está activo o fue bloqueado en Auth.
    // También puedes usar tu Enum UserStatus.
    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = "ACTIVE"; // Valor por defecto si usas String
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
