package com.budgie.server.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponseDto {
    private String email;
    private String nickname;

    private String accessToken;
    private String refreshToken;
    private String grantType;
    private Long accessTokenExpiresIn;
}
