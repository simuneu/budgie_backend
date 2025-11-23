package com.budgie.server.service;

import com.budgie.server.entity.AlertEntity;
import com.budgie.server.enums.AlertType;
import com.budgie.server.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AlertService {
    private final AlertRepository alertRepository;

    public void createAlert(Long userId, AlertType type, String message){
        //오늘 알림 여부 체크
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        boolean exists = alertRepository.existsByUserIdAndTypeAndCreatedAtBetween(
                userId, type, startOfDay, endOfDay);
        if(exists) return; //이미 보냈으면 안 보냄

        AlertEntity alert = AlertEntity.builder()
                .userId(userId)
                .type(type)
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        alertRepository.save(alert);
    }

}
