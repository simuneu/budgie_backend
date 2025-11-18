package com.budgie.server.service;

import com.budgie.server.dto.BudgetGoalDto;
import com.budgie.server.dto.GoalRequestDto;
import com.budgie.server.dto.GoalResponseDto;
import com.budgie.server.entity.BudgetGoalEntity;
import com.budgie.server.repository.BudgetGoalRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public GoalResponseDto updateGoal(Long userId, int year, int month, GoalRequestDto dto){
        BudgetGoalEntity goal = budgetGoalRepository.findByUserIdAndYearAndMonth(userId, year, month)
                .orElseThrow(() -> new EntityNotFoundException("목표를 찾을 수 없습니다."));
        goal.setGoalAmount(dto.getGoalAmount());
        budgetGoalRepository.save(goal);

        return GoalResponseDto.fromEntity(goal);
    }
}
