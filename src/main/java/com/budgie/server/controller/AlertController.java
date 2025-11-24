package com.budgie.server.controller;

import com.budgie.server.entity.AlertEntity;
import com.budgie.server.repository.AlertRepository;
import com.budgie.server.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/alert")
@RequiredArgsConstructor
public class AlertController {
    private final AlertRepository alertRepository;
    public final AlertService alertService;

    //알림 리스트 조회
    @GetMapping
    public ResponseEntity<List<AlertEntity>> getAlert(Principal principal){
        Long userId = Long.parseLong(principal.getName());

        List<AlertEntity> alerts = alertRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return ResponseEntity.ok(alerts);
    }

    //단일 알림 읽음처리
    @PostMapping("{id}/read")
    public ResponseEntity<Void> readAlert(@PathVariable Long id, Principal principal){
        Long userId = Long.parseLong(principal.getName());

        AlertEntity alert = alertRepository.findById(id)
                .orElseThrow(()->new RuntimeException("알림을 찾을 수 없습니다."));

        if(!alert.getUserId().equals(userId)){
            throw new RuntimeException("권한이 없습니다.");
        }
        alert.setRead(true);
        alertRepository.save(alert);

        return ResponseEntity.ok().build();
    }

    //전체 읽음 처리
    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(Principal principal){
        Long userId = Long.parseLong(principal.getName());
        alertService.markAllAsRead(userId);

        return ResponseEntity.ok("읽음 처리 완료");
    }

    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount(Principal principal){
        Long userId = Long.parseLong(principal.getName());
        return ResponseEntity.ok(alertService.getUnreadCount(userId));
    }
}

