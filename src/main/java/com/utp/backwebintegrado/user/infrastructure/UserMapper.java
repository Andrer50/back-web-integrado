package com.utp.backwebintegrado.user.infrastructure;

import com.utp.backwebintegrado.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", source = "userId")
    @Mapping(target = "role", source = "role") // <-- Toma el valor del parámetro
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    User toEntity(Object request, UUID userId, String role);

}
