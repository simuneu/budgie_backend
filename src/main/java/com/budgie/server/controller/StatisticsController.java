package com.budgie.server.controller;

import com.budgie.server.dto.CompareExpenseDto;
import com.budgie.server.dto.DailyExpenseDto;
import com.budgie.server.dto.TopCategoryDto;
import com.budgie.server.dto.WeeklyExpenseDto;
import com.budgie.server.service.StatisticsService;
import lombok.Getter;
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
    public ResponseEntity<List<DailyExpenseDto>> getDailyExpense(@PathVariable int year, @PathVariable int month,
                                                                 Principal principal){
        Long userId = Long.parseLong(principal.getName());
        List<DailyExpenseDto> result = statisticsService.getDailyExpense(userId, year, month);
        return ResponseEntity.ok(result);
    }

    //요일별 합계
    @GetMapping("/{year}/{month}/weekday")
    public ResponseEntity<List<WeeklyExpenseDto>> geyWeeklyExpense(@PathVariable int year, @PathVariable int month,
                                                                   Principal principal){
        Long userId = Long.parseLong(principal.getName());
        List<WeeklyExpenseDto> result = statisticsService.getWeeklyExpense(userId, year, month);
        return ResponseEntity.ok(result);
    }

    //탑3카테고리
    @GetMapping("/summary/top3/{year}/{month}")
    public List<TopCategoryDto> getTop3(@PathVariable  int year, @PathVariable int month, Principal principal){
        Long userId = Long.parseLong(principal.getName());
        return statisticsService.getTop3Categories(userId, year, month);
    }

    //전월 증감
    @GetMapping("/summary/compare/{year}/{month}")
    public CompareExpenseDto getCompare(@PathVariable int year, @PathVariable int month, Principal principal){
        Long userId = Long.parseLong(principal.getName());
        return statisticsService.getCompareExpense(userId, year, month);
    }
}
