package com.budgie.server.controller;

import com.budgie.server.dto.*;
import com.budgie.server.entity.UserEntity;
import com.budgie.server.service.AuthService;
import jakarta.validation.Valid;
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

    //로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto requestDto){
        try{
            AuthResponseDto responseDto   = authService.login(requestDto);
            return ResponseEntity.ok().body(responseDto);
        }catch (Exception e){
            ResponseDto responseDto = ResponseDto.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.status(401).body(responseDto);
        }
    }

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestBody LogoutRequestDto requestDto){
        Long userId = requestDto.getUserId();
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

    //토큰 갱신 -access+refresh발급
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody RefreshTokenRequestDto requestDto){
        String refreshToken = requestDto.getRefreshTokenDto();
        try{
            log.info("access token 갱신 요청 수신");
            AuthResponseDto responseDto = authService.refreshAccessToken(refreshToken);
            return ResponseEntity.ok().body(responseDto);
        } catch (RuntimeException e) {
            log.error("토큰 갱신 실패"+e.getMessage());
            ResponseDto responseDto = ResponseDto.builder()
                    .error("토큰 갱신 실패"+e.getMessage())
                    .build();
            //401/403
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDto);
        }
    }
}
