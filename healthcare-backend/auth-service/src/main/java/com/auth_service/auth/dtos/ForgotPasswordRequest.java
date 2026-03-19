package com.auth_service.auth.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ForgotPasswordRequest {
    private String email;
}
