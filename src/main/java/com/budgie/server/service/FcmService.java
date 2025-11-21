package com.budgie.server.service;

import com.budgie.server.entity.FcmTokenEntity;
import com.budgie.server.repository.FcmTokenRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FcmService {

    private final FcmTokenRepository fcmTokenRepository;

    public void saveToken(Long userId, String token){

        FcmTokenEntity entity = fcmTokenRepository.findByUserId(userId)
                .orElse(FcmTokenEntity.builder()
                        .userId(userId)
                        .build());
        entity.setToken(token);
        fcmTokenRepository.save(entity);
    }

    public void sendMessage(String token, String title, String body){
        Message message = Message.builder()
                .setToken(token)
                .putData("title", title)
                .putData("body", body)
                .build();

        try{
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("fcm 성공적으로 보내졌습니다. :{}", response);
        }catch (Exception e){
            log.error("fcm error:{}", e.getMessage() ,e);
        }
    }
}
