package com.budgie.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class WeekdayExpenseDto {
    private String weekday;
    private BigDecimal totalAmount;
}
