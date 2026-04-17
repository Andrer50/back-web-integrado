package com.utp.backwebintegrado.repository;

import com.utp.backwebintegrado.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);
    User getByEmail(String email);
}
