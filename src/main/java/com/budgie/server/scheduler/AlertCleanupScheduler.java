package com.budgie.server.scheduler;

import com.budgie.server.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertCleanupScheduler {
    private final AlertService alertService;

    @Scheduled(cron = "0 0 3 ? * MON")
    public void cleanUpOldAlerts(){
        alertService.deleteAlertsOlderThanDays(7);
    }
}
