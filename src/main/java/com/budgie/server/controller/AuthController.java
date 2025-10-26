package com.budgie.server.controller;

import com.budgie.server.dto.*;
import com.budgie.server.entity.UserEntity;
import com.budgie.server.service.AuthService;
import com.budgie.server.service.SocialLoginService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.GetExchange;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Value("${front.redirect.uri}")
    private String frontUri;

    private final AuthService authService;
    private final SocialLoginService socialLoginService;

    @Autowired
    public AuthController(AuthService authService, SocialLoginService socialLoginService){
        this.authService = authService;
        this.socialLoginService = socialLoginService;
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

    //카카오 소셜 로그인
    @GetMapping("/kakao")
    public void kakaoLogin(@RequestParam("code")String code, HttpServletResponse response) throws IOException{
        try{
            AuthResponseDto auth = socialLoginService.kakaoLogin(code);

            String accessToken = auth.getAccessToken();
            String refreshToken = auth.getRefreshToken();

            String redirectUrl = frontUri+"/oauth/callback"+
                    "?accessToken=" + URLEncoder.encode(accessToken, StandardCharsets.UTF_8) +
                    "&refreshToken=" + URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);
            response.sendRedirect(redirectUrl);
        }catch (Exception e){
            String errorRedirectUrl = frontUri + "/login?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect(errorRedirectUrl);
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
