package com.utp.backwebintegrado.service.impl;

import com.utp.backwebintegrado.dto.enumeration.Role;
import com.utp.backwebintegrado.dto.response.UserResponse;
import com.utp.backwebintegrado.entity.User;
import com.utp.backwebintegrado.repository.UserRepository;
import com.utp.backwebintegrado.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    @Override
    public UserResponse getByEmail(String email) {
        User user = userRepository.getByEmail(email);
        return UserResponse.builder()
                .email(user.getEmail())
                .role(Role.valueOf(user.getRole()))
                .id(String.valueOf(user.getId()))
                .build();
    }
}
