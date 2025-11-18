package com.budgie.server.service;

import com.budgie.server.dto.DailyExpenseDto;
import com.budgie.server.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final TransactionRepository transactionRepository;

    //일 합계조회
    public List<DailyExpenseDto> getDailyExpense(Long userId, int year, int month){
        return transactionRepository.getDailyExpense(userId, year, month);
    }
}
