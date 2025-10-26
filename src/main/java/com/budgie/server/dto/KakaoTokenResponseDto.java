package com.budgie.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class KakaoTokenResponseDto {
    @JsonProperty("accessToken")
    private String accessToken;

    @JsonProperty("token_type")
    private String refreshToken;
}
