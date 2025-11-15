package com.budgie.server.service;

import com.budgie.server.dto.BudgetGoalDto;
import com.budgie.server.entity.BudgetGoalEntity;
import com.budgie.server.repository.BudgetGoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BudgetGoalService {
    private final BudgetGoalRepository budgetGoalRepository;

    //목표 조회
    public BudgetGoalEntity getGoal(Long userId, Integer year, Integer month){
        return budgetGoalRepository
                .findByUserIdAndYearAndMonth(userId, year, month)
                .orElse(null);
    }

    //목표 저장
    public BudgetGoalEntity savedGoal(Long userId, BudgetGoalDto dto){
        BudgetGoalEntity entity = budgetGoalRepository
                .findByUserIdAndYearAndMonth(userId, dto.getYear(), dto.getMonth())
                .orElse(BudgetGoalEntity.builder()
                        .userId(userId)
                        .year(dto.getYear())
                        .month(dto.getMonth())
                        .build()
                );
        entity.setGoalAmount(dto.getGoalAmount());
        return budgetGoalRepository.save(entity);
    }
}
