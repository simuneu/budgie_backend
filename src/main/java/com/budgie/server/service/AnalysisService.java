package com.budgie.server.service;

import com.budgie.server.dto.SpendingPaceResponseDto;
import com.budgie.server.dto.WeekdayExpenseDto;
import com.budgie.server.entity.BudgetGoalEntity;
import com.budgie.server.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AnalysisService {
    private final TransactionRepository transactionRepository;
    private final BudgetGoalService budgetGoalService;


    //지출속도
    public SpendingPaceResponseDto getSpendingPace(Long userId, int year, int month){
        //이번 달 지출 총액
        long totalExpense = transactionRepository.getMonthlyExpense(userId, year, month);

        //목표 예산
        BudgetGoalEntity goal = budgetGoalService.getGoal(userId, year, month);
        long budgetGoal = (goal != null) ? goal.getGoalAmount() :0;

        //날짜 계산
        LocalDate today = LocalDate.now();
        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
        int passedDays = (year == today.getYear() && month == today.getMonthValue())
                ?today.getDayOfMonth()
                :daysInMonth;

        long dailyAvg = (passedDays == 0)? 0:totalExpense / passedDays;
        long expectedEnd = dailyAvg * daysInMonth;

        //상태 위험 계산
        String paceStatus = calculatePaceStatus(expectedEnd, budgetGoal);
        String dangerLevel = calculateDangerLevel(expectedEnd,budgetGoal);

        return SpendingPaceResponseDto.builder()
                .year(year)
                .month(month)
                .totalExpense(totalExpense)
                .budgetGoal(budgetGoal)
                .dailyAvg(dailyAvg)
                .expectedEndOfMonth(expectedEnd)
                .paceStatus(paceStatus)
                .dangerLevel(dangerLevel)
                .build();
    }

    private String calculatePaceStatus(long expectedEnd, long budgetGoal){
        if(budgetGoal == 0) return "NORMAL";
        if(expectedEnd > budgetGoal) return "OVER";
        if(expectedEnd < budgetGoal*0.7) return "UNDER";
        return "NORMAL";
    }

    private String calculateDangerLevel(long expectedEnd, long budgetGoal){
        if(budgetGoal == 0) return "LOW";
        double ratio = (double) expectedEnd / budgetGoal;

        if(ratio>=1.2) return "HIGH";
        if(ratio>=1.0) return "MID";
        return "LOW";
    }

    //요일별 소비패턴
    public List<WeekdayExpenseDto> getWeekdayPattern(Long userId, int year, int month){
        List<Object[]> results = transactionRepository.getWeekdayExpense(userId, year, month);

        List<WeekdayExpenseDto> response = new ArrayList<>();

        for(Object[]row: results) {
            int weekday = ((Number)row[0]).intValue();

            BigDecimal totalAmount = (row[1] !=null)
                    ?(BigDecimal) row[1]
                    :BigDecimal.ZERO;

            response.add(new WeekdayExpenseDto(convertWeekday(weekday), totalAmount));
        }
        return response;
    }

    private String convertWeekday(int weekday) {
        return switch (weekday) {
            case 0 -> "MONDAY";
            case 1 -> "TUESDAY";
            case 2 -> "WEDNESDAY";
            case 3 -> "THURSDAY";
            case 4 -> "FRIDAY";
            case 5 -> "SATURDAY";
            case 6 -> "SUNDAY";
            default -> "UNKNOWN";
        };
    }
}
