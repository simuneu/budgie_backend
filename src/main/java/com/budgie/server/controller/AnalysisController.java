package com.budgie.server.controller;

import com.budgie.server.dto.DailyTrendDto;
import com.budgie.server.dto.MonthlyTrendDto;
import com.budgie.server.dto.SpendingPaceResponseDto;
import com.budgie.server.dto.WeekdayExpenseDto;
import com.budgie.server.service.AnalysisService;
import lombok.RequiredArgsConstructor;
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
    public SpendingPaceResponseDto getSpendingPace(@RequestParam int year, @RequestParam int month,
                                                   Principal principal){
        Long userId = Long.parseLong(principal.getName());
        return analysisService.getSpendingPace(userId, year,month);
    }

    //요일소비패턴
    @GetMapping("/weekday-pattern")
    public List<WeekdayExpenseDto> getWeekdayPattern(@RequestParam int year, @RequestParam int month, Principal principal){
        Long userId = Long.parseLong(principal.getName());
        return analysisService.getWeekdayPattern(userId, year, month);
    }

    //날짜별 소비 경향
    @GetMapping("/daily-trend")
    public List<DailyTrendDto> getDailyTrend(@RequestParam int year, @RequestParam int month, Principal principal){
        Long userId = Long.parseLong(principal.getName());
        return analysisService.getDailyTrend(userId, year, month);
    }

    //월별 소비 추세
    @GetMapping("/monthly-trend")
    public List<MonthlyTrendDto> getMonthlyTrend( @RequestParam int year,@RequestParam int month,
                                                  @RequestParam(required = false, defaultValue = "3")int count,
                                                  Principal principal){
        Long userId = Long.parseLong(principal.getName());
        return analysisService.getMonthlyTrend(userId, year, month, count);
    }
}
