package com.budgie.server.controller;

import com.budgie.server.dto.ApiResponse;
import com.budgie.server.entity.UserEntity;
import com.budgie.server.repository.UserRepository;
import com.budgie.server.service.FcmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fcm")
public class FcmController {

    private final FcmService fcmService;
    private final UserRepository userRepository;

    @PostMapping("/token")
    public ResponseEntity<ApiResponse<Void>> saveFcmToken(@RequestBody Map<String, String> body, Principal principal){
        if (principal == null) {
            return ResponseEntity.ok(ApiResponse.okMessage("인증되지 않은 사용자"));
        }

        Long userId = Long.parseLong(principal.getName());
        String token = body.get("token");

        fcmService.saveToken(userId, token);

        return ResponseEntity.ok(ApiResponse.okMessage("FCM 토큰 저장 완료"));
    }

}
