package com.budgie.server.dto;

import lombok.Getter;

@Getter
public class PasswordResetRequestDto {
    private String email;
    private String code;
    private String newPassword;
}
