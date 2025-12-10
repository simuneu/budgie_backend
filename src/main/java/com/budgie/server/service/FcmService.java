package com.budgie.server.service;

import com.budgie.server.entity.UserEntity;
import com.budgie.server.repository.UserRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FcmService {

    private final UserRepository userRepository;

    public void saveToken(Long userId, String token){

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        user.setFcmToken(token);
        userRepository.save(user);
    }

    public void send(String token, String title, String body){
        Message message = Message.builder()
                .setToken(token)
                .putData("title", title)
                .putData("body", body)
                                .build();

        try{
            String response = FirebaseMessaging.getInstance().send(message);
            log.debug("fcm 성공적으로 보내졌습니다. :{}", response);
        }catch (Exception e){
            log.error("fcm error:{}", e.getMessage() ,e);
        }
    }
}
