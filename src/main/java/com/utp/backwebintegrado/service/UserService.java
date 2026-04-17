package com.utp.backwebintegrado.service;


import com.utp.backwebintegrado.dto.response.UserResponse;

public interface UserService {
    UserResponse getByEmail (String email);
}
