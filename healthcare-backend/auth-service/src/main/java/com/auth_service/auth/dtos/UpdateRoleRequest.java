package com.auth_service.auth.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRoleRequest {
    private String username;
    private Role role;
}
