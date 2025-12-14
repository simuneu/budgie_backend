package com.budgie.server.controller;

import com.budgie.server.dto.*;
import com.budgie.server.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

    //일 합계조회 - 대시보드 달력(소비패턴에서는 라인그래프 적용)
    @GetMapping("/{year}/{month}/daily")
    public ResponseEntity<ApiResponse<List<DailyExpenseDto>>> getDailyExpense(@PathVariable int year, @PathVariable int month,
                                                                              Principal principal){
        Long userId = Long.parseLong(principal.getName());
        List<DailyExpenseDto> result = statisticsService.getDailyExpense(userId, year, month);

        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    //요일별 합계
    @GetMapping("/{year}/{month}/weekday")
    public ResponseEntity<ApiResponse<List<WeeklyExpenseDto>>> geyWeeklyExpense(@PathVariable int year, @PathVariable int month,
                                                                   Principal principal){
        Long userId = Long.parseLong(principal.getName());
        List<WeeklyExpenseDto> result = statisticsService.getWeeklyExpense(userId, year, month);

        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    //탑3카테고리
    @GetMapping("/summary/top3/{year}/{month}")
    public  ResponseEntity<ApiResponse<List<TopCategoryDto>>> getTop3(@PathVariable  int year, @PathVariable int month, Principal principal){
        Long userId = Long.parseLong(principal.getName());
        List<TopCategoryDto> result =
                statisticsService.getTop3Categories(userId, year, month);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    //전월 증감
    @GetMapping("/summary/compare/{year}/{month}")
    public ResponseEntity<ApiResponse<CompareExpenseDto>> getCompare(@PathVariable int year, @PathVariable int month, Principal principal){
        Long userId = Long.parseLong(principal.getName());
        CompareExpenseDto result =
                statisticsService.getCompareExpense(userId, year, month);
        return ResponseEntity.ok(ApiResponse.ok(result));    }
}
