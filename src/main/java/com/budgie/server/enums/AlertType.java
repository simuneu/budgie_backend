package com.budgie.server.enums;

public enum AlertType {
    BUDGET_DANGER, // 예산 초과 위험
    WEEKDAY_PATTERN, // 특정 요일 과소비
    DAILY_SPIKE, // 최근 3일 급증
    MONTHLY_SPIKE // 지난달 대비 급증
}
