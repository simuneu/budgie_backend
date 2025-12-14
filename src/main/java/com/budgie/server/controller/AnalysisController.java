package com.budgie.server.controller;

import com.budgie.server.dto.*;
import com.budgie.server.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/analysis")
@RequiredArgsConstructor
public class AnalysisController {
    private final AnalysisService analysisService;

    //지출 속도
    @GetMapping("/spending-pace")
    public ResponseEntity<ApiResponse<SpendingPaceResponseDto>> getSpendingPace(@RequestParam int year, @RequestParam int month,
                                                                                Principal principal){
        Long userId = Long.parseLong(principal.getName());
        SpendingPaceResponseDto result = analysisService.getSpendingPace(userId, year, month);

        return ResponseEntity.ok(ApiResponse.ok(result));

    }

    //요일소비패턴
    @GetMapping("/weekday-pattern")
    public ResponseEntity<ApiResponse<List<WeekdayExpenseDto>>> getWeekdayPattern(@RequestParam int year, @RequestParam int month, Principal principal){
        Long userId = Long.parseLong(principal.getName());
        List<WeekdayExpenseDto> result =
                analysisService.getWeekdayPattern(userId, year, month);

        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    //날짜별 소비 경향
    @GetMapping("/daily-trend")
    public ResponseEntity<ApiResponse<List<DailyTrendDto>>> getDailyTrend(@RequestParam int year, @RequestParam int month, Principal principal){
        Long userId = Long.parseLong(principal.getName());

        List<DailyTrendDto> result =
                analysisService.getDailyTrend(userId, year, month);

        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    //월별 소비 추세
    @GetMapping("/monthly-trend")
    public ResponseEntity<ApiResponse<List<MonthlyTrendDto>>> getMonthlyTrend( @RequestParam int year,@RequestParam int month,
                                                  @RequestParam(required = false, defaultValue = "3")int count,
                                                  Principal principal){
        Long userId = Long.parseLong(principal.getName());
        List<MonthlyTrendDto> result =
                analysisService.getMonthlyTrend(userId, year, month, count);

        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
