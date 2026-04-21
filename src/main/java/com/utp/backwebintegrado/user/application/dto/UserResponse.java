package com.utp.backwebintegrado.user.application.dto;

import com.utp.backwebintegrado.shared.enumeration.Role;
import com.utp.backwebintegrado.shared.enumeration.Status;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private Status status;
}
