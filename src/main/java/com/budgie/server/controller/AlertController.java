package com.budgie.server.controller;

import com.budgie.server.dto.ApiResponse;
import com.budgie.server.entity.AlertEntity;
import com.budgie.server.repository.AlertRepository;
import com.budgie.server.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<List<AlertEntity>>> getAlert(Principal principal){
        Long userId = Long.parseLong(principal.getName());

        List<AlertEntity> alerts = alertRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return ResponseEntity.ok(ApiResponse.ok(alerts));
    }

    //단일 알림 읽음처리
    @PostMapping("{id}/read")
    public ResponseEntity<ApiResponse<Void>> readAlert(@PathVariable Long id, Principal principal){
        Long userId = Long.parseLong(principal.getName());

        AlertEntity alert = alertRepository.findById(id)
                .orElseThrow(()->new RuntimeException("알림을 찾을 수 없습니다."));

        if(!alert.getUserId().equals(userId)){
            throw new RuntimeException("권한이 없습니다.");
        }
        alert.setRead(true);
        alertRepository.save(alert);

        return ResponseEntity.ok(ApiResponse.okMessage("읽음 처리 완료"));
    }

    //전체 읽음 처리
    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(Principal principal){
        Long userId = Long.parseLong(principal.getName());
        alertService.markAllAsRead(userId);

        return ResponseEntity.ok(ApiResponse.okMessage("전체 읽음 처리 완료"));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(Principal principal){
        Long userId = Long.parseLong(principal.getName());
        Long count = (long) alertService.getUnreadCount(userId);

        return ResponseEntity.ok(ApiResponse.ok(count));
    }

    //알림 지우기
    @DeleteMapping("/{alertId}")
    public ResponseEntity<ApiResponse<Void>> deleteAlert(@PathVariable Long alertId, Principal principal){
        Long userId = Long.parseLong(principal.getName());
        alertService.deleteAlert(userId,alertId);
        return ResponseEntity.ok(ApiResponse.okMessage("삭제 완료"));
    }
}

