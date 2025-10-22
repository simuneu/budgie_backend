package com.budgie.server.dto;

import jdk.jfr.StackTrace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignupRequestDto {
    private String email;

    private String password;

    private String nickname;

    private String validCode;


}
