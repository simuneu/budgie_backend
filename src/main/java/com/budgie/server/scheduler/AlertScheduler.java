package com.budgie.server.scheduler;

import com.budgie.server.dto.SpendingPaceResponseDto;
import com.budgie.server.entity.UserEntity;
import com.budgie.server.enums.AlertType;
import com.budgie.server.repository.UserRepository;
import com.budgie.server.service.AlertService;
import com.budgie.server.service.AnalysisService;
import com.budgie.server.service.FcmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertScheduler {
    private final AnalysisService analysisService;
    private final AlertService alertService;
    private final FcmService fcmService;
    private  final UserRepository userRepository;


    @Scheduled(cron = "0 0 9,12,14,18,22 * * *")
//    @Scheduled(cron = "0 0 9 * * *") //초 분 시 일 월 요일 - 9시
    public void runDailyAlertCheck() {
        log.info("AlertScheduler : 매일 체크를 시작");

        List<UserEntity> users = userRepository.findAll();

        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        for (UserEntity user : users) {
            Long userId = user.getUserId();

            //유저 지출 속도 분석
            SpendingPaceResponseDto pace = analysisService.getSpendingPace(userId, year, month);

            long used = pace.getTotalExpense();
            long goal = pace.getBudgetGoal();

            // 목표 금액 없으면 스킵
            if (goal == 0) continue;

            int rate = analysisService.calculateUsageRate(used, goal);

            //위헙도 알림
            if ("HIGH".equals(pace.getDangerLevel())) {
                String message = "이번 달 지출 속도가 너무 빨라요! 예산을 초과하기 일보직전이에요!!";

                alertService.createAlert(userId, AlertType.BUDGET_DANGER, message);

                if (user.getFcmToken() != null) {
                    fcmService.send(
                            user.getFcmToken(),
                            "\uD83D\uDD25예산 초과 위험 !!\uD83D\uDD25",
                            message
                    );
                }
                log.info("user {}: 예산 초과 위험 알림 전송", userId);
            }
        }//for
    }
}
