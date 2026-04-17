package com.utp.backwebintegrado.dto.response;

import com.utp.backwebintegrado.dto.enumeration.Role;
import com.utp.backwebintegrado.dto.enumeration.Status;
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
