package com.budgie.server.controller;

import com.budgie.server.dto.BudgetGoalDto;
import com.budgie.server.entity.BudgetGoalEntity;
import com.budgie.server.service.BudgetGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/budget/goal")
@RequiredArgsConstructor
public class BudgetGoalController {
    private final BudgetGoalService budgetGoalService;

    //목표 조회
    @GetMapping
    public BudgetGoalDto getGoal(@RequestParam Integer year, @RequestParam Integer month, Principal principal){
        Long userId = Long.parseLong(principal.getName());

        Optional<BudgetGoalEntity> goal = budgetGoalService.getMonthlyGoal(userId, year, month);

        return goal.map(g -> BudgetGoalDto.builder()
                .year(g.getYear())
                .month(g.getMonth())
                .goalAmount(g.getGoalAmount())
                .build())
                .orElse(null); //없으면 null 프론트에서 모달 띄우게 하기
    }

    //목표 저장
    @PostMapping
    public BudgetGoalDto setGoal(@RequestBody BudgetGoalDto dto, Principal principal){
        Long userId = Long.parseLong(principal.getName());
        BudgetGoalEntity saved = budgetGoalService.savedGoal(userId, dto);

        return BudgetGoalDto.builder()
                .year(saved.getYear())
                .month(saved.getMonth())
                .goalAmount(saved.getGoalAmount())
                .build();
    }
}
