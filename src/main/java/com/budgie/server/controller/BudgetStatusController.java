package com.budgie.server.controller;

import com.budgie.server.entity.BudgetGoalEntity;
import com.budgie.server.service.BudgetGoalService;
import com.budgie.server.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/budget")
@RequiredArgsConstructor
public class BudgetStatusController {
    private final BudgetGoalService budgetGoalService;
    private final TransactionService transactionService;

    //이번 달 예산 현황 조회
    @GetMapping("/status")
    public Map<String, Object> getStatus(@RequestParam Integer year, @RequestParam Integer month, Principal principal){
        Long userId = Long.parseLong(principal.getName());

        //목표 가져오기
        BudgetGoalEntity goal = budgetGoalService.getGoal(userId, year, month);

        //달 총 지출액
        Long totalExpense = transactionService.getMonthlyExpense(userId, year, month);

        Map<String, Object> result = new HashMap<>();
        result.put("goalAmount", goal != null ? goal.getGoalAmount() : null);
        result.put("totalExpense", totalExpense);

        return result;
    } //http://localhost:8080/api/budget/status?year=2025&month=11

}
