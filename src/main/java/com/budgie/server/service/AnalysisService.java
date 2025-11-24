package com.budgie.server.service;

import com.budgie.server.dto.DailyTrendDto;
import com.budgie.server.dto.MonthlyTrendDto;
import com.budgie.server.dto.SpendingPaceResponseDto;
import com.budgie.server.dto.WeekdayExpenseDto;
import com.budgie.server.entity.BudgetGoalEntity;
import com.budgie.server.enums.DangerLevel;
import com.budgie.server.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        Long monthlyExpense = transactionRepository.getMonthlyExpense(userId, year, month);
        long totalExpense = (monthlyExpense != null) ? monthlyExpense : 0L;

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
        DangerLevel dangerLevel = calculateDangerLevel(
                BigDecimal.valueOf(expectedEnd),
                BigDecimal.valueOf(budgetGoal)
        );

        return SpendingPaceResponseDto.builder()
                .year(year)
                .month(month)
                .totalExpense(totalExpense)
                .budgetGoal(budgetGoal)
                .dailyAvg(dailyAvg)
                .expectedEndOfMonth(expectedEnd)
                .paceStatus(paceStatus)
                .dangerLevel(dangerLevel.name())
                .build();
    }

    private String calculatePaceStatus(long expectedEnd, long budgetGoal){
        if(budgetGoal == 0) return "NORMAL";
        if(expectedEnd > budgetGoal) return "OVER";
        if(expectedEnd < budgetGoal*0.7) return "UNDER";
        return "NORMAL";
    }

    //위헙레벨 계산
    private DangerLevel calculateDangerLevel(BigDecimal expectedEnd, BigDecimal budgetGoal){
        if(budgetGoal.compareTo(BigDecimal.ZERO)==0){
            return DangerLevel.LOW;
        }

        BigDecimal ratio = expectedEnd.divide(budgetGoal, 2, RoundingMode.HALF_UP);

        //예산 초과
        if(ratio.compareTo(BigDecimal.ONE) > 0){
            return DangerLevel.HIGH;
        }
        //근접
        if(ratio.compareTo(BigDecimal.ONE) == 0){
            return DangerLevel.MID;
        }
        return DangerLevel.LOW;
    }

    //%알림
    public int calculateUsageRate(long totalExpense, long budgetGoal){
        if(budgetGoal == 0){
            return 0;
        }
        return (int)((totalExpense*100)/budgetGoal);
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

    //날짜별 소비경향
    public List<DailyTrendDto> getDailyTrend(Long userId, int year, int month){
        List<Object[]> results = transactionRepository.getDailyTrend(userId, year, month);

        List<DailyTrendDto> response = new ArrayList<>();

        //조회된 날만 응담(0원인 날 제외)
        for(Object[] row:results){
            int day = ((Number)row[0]).intValue();

            BigDecimal total =(row[1]!=null)
                    ?(BigDecimal)row[1]
                    :BigDecimal.ZERO;

            response.add(new DailyTrendDto(day, total));
        }
        return response;
    }

    //월별 지출 추세
    public List<MonthlyTrendDto> getMonthlyTrend(Long userId, int year, int month, int count){
        if(count<=0){
            count=3; //3개월
        }
        YearMonth current = YearMonth.of(year, month);
        List<MonthlyTrendDto> result = new ArrayList<>();

        //오>최
        for(int i = count-1; i>=0; i--){
            YearMonth target = current.minusMonths(i);

            Long total = transactionRepository.getMonthlyExpense(userId, target.getYear(), target.getMonthValue());
            if(total == null){
                total =0L;
            }
            BigDecimal totalAmount = BigDecimal.valueOf(total);

            result.add(new MonthlyTrendDto(
                    target.getYear(),
                    target.getMonthValue(),
                    totalAmount
            ));
        }
        return result;
    }
}
