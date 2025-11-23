package com.budgie.server.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SpendingPaceResponseDto {
    private  int year;
    private int month;

    private long totalExpense; //현 총 지출
    private long budgetGoal; //예산 목표
    private long dailyAvg; //하루평균 지출
    private long expectedEndOfMonth; //예상 월 지출

    private String paceStatus;// NORMAL / OVER / UNDER
    private String dangerLevel; // LOW / MID / HIGH
}
