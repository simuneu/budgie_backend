package com.budgie.server.controller;

import com.budgie.server.dto.EmailRequestDto;
import com.budgie.server.dto.EmailVerificationRequestDto;
import com.budgie.server.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/email")
public class EmailController {

    private final AuthService authService;

    //인증코드 발송
    @PostMapping("/send")
    public ResponseEntity<String> sendVerificationEmail(@Valid @RequestBody EmailRequestDto requestDto){
        authService.sendVerificationEmail(requestDto.getEmail());

        return ResponseEntity.ok("이메일이 성공적으로 발송되었습니다.");
    }

    //인증코드 검증
    @PostMapping("/verify")
    public ResponseEntity<String> verifyEmailCode(@Valid @RequestBody EmailVerificationRequestDto requestDto){
        authService.completeEmailVerification(requestDto.getEmail(), requestDto.getCode());
        return ResponseEntity.ok("이메일 인증 완료");
    }
}
