package com.utp.backwebintegrado.user.application.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AuthRegisterRequest {
    private String email;
    private String password;
    private String role;
    private UUID externalId; // El UUID generado por el microservicio de negocio

}