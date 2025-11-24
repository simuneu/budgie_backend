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

            int rate = analysisService.calculateUsageRate(used, goal);

            //위헙도 알림
            if ("HIGH".equals(pace.getDangerLevel())) {
                if (!alertService.existsMonthlyAlert(userId, AlertType.BUDGET_DANGER)) {
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
            }
            //% 누적 알림
            if (goal == 0) continue; // 목표 금액 없는 유저는 알림 스킵

            if (rate >= 70 && rate < 80) {
                if (!alertService.existsMonthlyAlert(userId, AlertType.BUDGET_70)) {
                    sendUsageAlert(user, 70, AlertType.BUDGET_70);
                }
            } else if (rate >= 80 && rate < 90) {
                if (!alertService.existsMonthlyAlert(userId, AlertType.BUDGET_80)) {
                    sendUsageAlert(user, 80, AlertType.BUDGET_80);
                }
            } else if (rate >= 90 && rate < 100) {
                if (!alertService.existsMonthlyAlert(userId, AlertType.BUDGET_90)) {
                    sendUsageAlert(user, 90, AlertType.BUDGET_90);
                }
            }
        }//for
    }

    private void sendUsageAlert(UserEntity user, int percent, AlertType type){
        Long userId = user.getUserId();

        String msg = "\uD83D\uDC25 이번 달 예산의 "+percent+"%를 사용했어요! 지출 조절이 필요해요!!";

        alertService.createAlert(userId, type, msg);

        if(user.getFcmToken() != null){
            fcmService.send(user.getFcmToken(), "예산 "+percent+"%사용", msg);
        }
    }
}
