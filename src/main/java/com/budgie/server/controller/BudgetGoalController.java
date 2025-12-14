package com.budgie.server.controller;

import com.budgie.server.dto.ApiResponse;
import com.budgie.server.dto.BudgetGoalDto;
import com.budgie.server.dto.GoalRequestDto;
import com.budgie.server.dto.GoalResponseDto;
import com.budgie.server.entity.BudgetGoalEntity;
import com.budgie.server.service.BudgetGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/budget/goal")
@RequiredArgsConstructor
public class BudgetGoalController {
    private final BudgetGoalService budgetGoalService;

    //목표 조회
    @GetMapping
    public ResponseEntity<ApiResponse<BudgetGoalDto>> getGoal(@RequestParam Integer year, @RequestParam Integer month, Principal principal){
        Long userId = Long.parseLong(principal.getName());

        BudgetGoalEntity goal = budgetGoalService.getGoal(userId, year, month);

        if(goal == null){
            return ResponseEntity.ok(ApiResponse.ok(null));
        }

        BudgetGoalDto dto = BudgetGoalDto.builder()
                .year(goal.getYear())
                .month(goal.getMonth())
                .goalAmount(goal.getGoalAmount())
                .build();

        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    //목표 저장
    @PostMapping
    public ResponseEntity<ApiResponse<BudgetGoalDto>> setGoal(@RequestBody BudgetGoalDto dto, Principal principal){
        Long userId = Long.parseLong(principal.getName());
        BudgetGoalEntity saved = budgetGoalService.savedGoal(userId, dto);

        BudgetGoalDto res = BudgetGoalDto.builder()
                .year(saved.getYear())
                .month(saved.getMonth())
                .goalAmount(saved.getGoalAmount())
                .build();
        return ResponseEntity.ok(ApiResponse.ok(res));
    }

    //목표 수정
    @PutMapping("/{year}/{month}")
    public ResponseEntity<ApiResponse<GoalResponseDto>> updateGoal(Principal principal, @PathVariable int year,
                                        @PathVariable int month, @RequestBody GoalRequestDto request){
        Long userId = Long.parseLong(principal.getName());

        GoalResponseDto updated = budgetGoalService.updateGoal(userId, year, month, request);

        return ResponseEntity.ok(ApiResponse.ok(updated));
    }
}
