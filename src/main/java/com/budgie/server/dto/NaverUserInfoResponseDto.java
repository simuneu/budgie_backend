package com.budgie.server.dto;

import lombok.Data;

@Data
public class NaverUserInfoResponseDto {
    private String resultCode;
    private String message;
    private Response response;

    @Data
    public static class Response{
        private String id;
        private String email;
        private String nickname;
    }
}
