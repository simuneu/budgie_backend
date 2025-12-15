package com.budgie.server.service;

import com.budgie.server.enums.AlertType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@RequiredArgsConstructor
public class AlertDispatchService {
    private final AlertService alertService;
    private final FcmService fcmService;

    @Async
    public void dispatchAlert(Long userId, AlertType type, String message) {
        alertService.createAlert(userId, type, message);

        fcmService.sendToUser(userId, type.name(), message);
        log.debug("dispatch thread = {}", Thread.currentThread().getName());
    }
}
