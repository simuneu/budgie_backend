package com.budgie.server.controller;

import com.budgie.server.dto.AuthResponseDto;
import com.budgie.server.dto.UserDto;
import com.budgie.server.dto.UserSignupRequestDto;
import com.budgie.server.entity.UserEntity;
import com.budgie.server.repository.AuthRepository;
import com.budgie.server.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserSignupRequestDto requestDto){
        log.info("회원가입 요청:{}", requestDto);

        try{
            UserEntity savedUser = authService.signup(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        }catch (RuntimeException e){
            log.error("회원 가입 실패:{}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody UserDto userDto){
        try{
            AuthResponseDto responseDto   = authService.login
        }catch (Exception e){

        }
    }
}
