package com.budgie.server.controller;

import com.budgie.server.dto.AuthResponseDto;
import com.budgie.server.dto.ResponseDto;
import com.budgie.server.dto.UserDto;
import com.budgie.server.dto.UserSignupRequestDto;
import com.budgie.server.entity.UserEntity;
import com.budgie.server.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
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

    //로그인
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDto userDto){
        try{
            AuthResponseDto responseDto   = authService.login(userDto.getEmail(), userDto.getPassword());
            return ResponseEntity.ok().body(responseDto);
        }catch (Exception e){
            ResponseDto responseDto = ResponseDto.builder()
                    .error("로그인 실패, 이메일과 비밀번호를 다시 확인")
                    .build();
            return ResponseEntity.status(401).body(responseDto);
        }
    }

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestBody Long userId){
        try{
            log.info("로그아웃 요청 수신. userId:{}", userId);
            authService.logout(userId);
            ResponseDto responseDto = ResponseDto.builder()
                    .message("로그아웃 성공")
                    .build();
            return ResponseEntity.ok().body(responseDto);
        }catch (Exception e){
            log.error("로그아웃 중 오류 발생 :"+e.getMessage());
            ResponseDto responseDto = ResponseDto.builder()
                    .error("로그아웃 처리 실패: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }
}
