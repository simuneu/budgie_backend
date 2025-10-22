package com.budgie.server.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken;
    private String grantType;
    private Long accessTokenExpiresIn;
}
