package com.budgie.server.service;

import com.budgie.server.dto.DailyExpenseDto;
import com.budgie.server.dto.WeeklyExpenseDto;
import com.budgie.server.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

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
        List<Object[]> rows = transactionRepository.getWeeklyExpense(userId, year, month);

        return rows.stream().map(r -> new WeeklyExpenseDto(
                        ((Number) r[0]).intValue(),
                        (r[1] instanceof BigDecimal)
                                ? (BigDecimal) r[1]
                                : BigDecimal.valueOf(((Number) r[1]).longValue())
                ))
                .toList();
    }
}
