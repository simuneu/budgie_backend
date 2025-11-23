package com.budgie.server.controller;

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
    public ResponseEntity<?> saveFcmToken(@RequestBody Map<String, String> body, Principal principal){
        if (principal == null) {
            return ResponseEntity.ok().build();
        }

        Long userId = Long.parseLong(principal.getName());
        String token = body.get("token");

        fcmService.saveToken(userId, token);

        return ResponseEntity.ok().build();
    }

    //테스트
    @PostMapping("/test")
    public ResponseEntity<?> testSend(Principal principal) {

        Long userId = Long.parseLong(principal.getName());

        // DB에서 fcm_token 읽기
        String token = userRepository.findById(userId)
                .map(UserEntity::getFcmToken)
                .orElse(null);

        if (token == null) {
            return ResponseEntity.badRequest().body("FCM token not found for user");
        }

        fcmService.send(
                token,
                "Budgie 테스트 알림 🔔",
                "이 알림이 보이면 FCM 연동 성공입니다!"
        );

        return ResponseEntity.ok("테스트 알림 발송됨");
    }

}
