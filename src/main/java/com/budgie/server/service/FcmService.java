package com.budgie.server.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FcmService {
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
