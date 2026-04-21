package com.utp.backwebintegrado.user.application;

import com.utp.backwebintegrado.user.application.dto.UserResponse;
import com.utp.backwebintegrado.shared.enumeration.Role;
import com.utp.backwebintegrado.user.domain.User;
import com.utp.backwebintegrado.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public UserResponse getByEmail(String email) {
        User user = userRepository.getByEmail(email);
        return UserResponse.builder()
                .email(user.getEmail())
                .role(Role.valueOf(user.getRole()))
                .id(String.valueOf(user.getId()))
                .build();
    }
}
