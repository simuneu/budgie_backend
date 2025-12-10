package com.budgie.server.service;

import com.budgie.server.entity.AlertEntity;
import com.budgie.server.enums.AlertType;
import com.budgie.server.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {
    private final AlertRepository alertRepository;

    public void createAlert(Long userId, AlertType type, String message){
        //오늘 알림 여부 체크
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        AlertEntity alert = AlertEntity.builder()
                .userId(userId)
                .type(type)
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        alertRepository.save(alert);
    }

    public boolean existsMonthlyAlert(Long userId, AlertType type){
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = today.withDayOfMonth(today.lengthOfMonth()).atTime(23, 59, 59);

        Long count = alertRepository.countMonthlyAlert(userId, type, start, end);

        return count != null && count > 0;
    }

    //읽음 전체 처리
    public void markAllAsRead(Long userId){
        List<AlertEntity> alerts = alertRepository.findByUserIdOrderByCreatedAtDesc(userId);
        alerts.forEach(alert -> {
            if(!alert.isRead()) alert.setRead(true);
        });
        alertRepository.saveAll(alerts);
    }

    //알림 목록 조회
    public List<AlertEntity> getAlerts(Long userId){
        return alertRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    //읽지 않은 알림 수
    public int getUnreadCount(Long userId){
        return alertRepository.countByUserIdAndIsReadFalse(userId);
    }

    //알림 지우기
    public void deleteAlert(Long userId, Long alertId){
        AlertEntity alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없음"));

        if(!alert.getUserId().equals(userId)){
            throw new RuntimeException("삭제 권한 없음");
        }
        alertRepository.delete(alert);
    }
}
