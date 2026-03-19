package com.auth_service.auth.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChangePasswordRequest {

    private String oldPassword;
    private String newPassword;
    private String confirmNewPassword;

}
