package com.budgie.server.service;

import com.budgie.server.dto.CompareExpenseDto;
import com.budgie.server.dto.DailyExpenseDto;
import com.budgie.server.dto.TopCategoryDto;
import com.budgie.server.dto.WeeklyExpenseDto;
import com.budgie.server.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final TransactionRepository transactionRepository;

    //일 합계조회
    public List<DailyExpenseDto> getDailyExpense(Long userId, int year, int month){
        return transactionRepository.getDailyExpense(userId, year, month);
    }

    //요일별 조회
    public List<WeeklyExpenseDto> getWeeklyExpense(Long userId, int year, int month){
        List<Object[]> rows = transactionRepository.getWeekdayExpense(userId, year, month);

        List<WeeklyExpenseDto> result = new ArrayList<>();

        for (Object[] r : rows) {
            int weekday0to6 = ((Number) r[0]).intValue();
            int weekday = weekday0to6 + 1;

            BigDecimal totalAmount =
                    (r[1] instanceof BigDecimal)
                            ? (BigDecimal) r[1]
                            : BigDecimal.valueOf(((Number) r[1]).longValue());

            result.add(new WeeklyExpenseDto(weekday, totalAmount));
        }

        return result;
    }

    //카테고리 탑3
    public List<TopCategoryDto> getTop3Categories(Long userId, int year, int month){
        List<Object[]> rows = transactionRepository.getTop3Categories(userId, year, month);
        return rows.stream().map(r->new TopCategoryDto(
                (String) r[0],
                ((Number) r[1]).longValue()
        )).toList();
    }

    //전월 증감
    public CompareExpenseDto getCompareExpense(Long userId, int year, int month){
        int prevYear = year;
        int prevMonth = month -1;

        if(month == 1){
            prevYear = year-1;
            prevMonth = 12;
        }

        //이번 달 총 지출
        Long current = transactionRepository.getMonthlyExpense(userId, year, month);
        if(current == null){
            current = 0L;
        }
        //저번 달 총 지출
        Long previous = transactionRepository.getMonthlyExpense(userId,  prevYear, prevMonth);
        if(previous == null){
            previous = 0L;
        }
        //차이
        Long difference = current - previous;

        //변화율
        double percent = (previous==0) ? 0.0 : (difference / (double)previous) *100;

        return CompareExpenseDto.builder()
                .current(current)
                .previous(previous)
                .difference(difference)
                .percent(Math.round(percent*10)/10.0)
                .build();
    }
}
