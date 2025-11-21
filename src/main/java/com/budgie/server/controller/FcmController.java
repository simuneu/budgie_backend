package com.budgie.server.controller;

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

    @PostMapping("/token")
    public ResponseEntity<?> saveFcmToken(@RequestBody Map<String, String> body, Principal principal){
        Long userId = Long.parseLong(principal.getName());
        String token = body.get("token");

        fcmService.saveToken(userId, token);

        return ResponseEntity.ok().build();
    }
}
