package com.budgie.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class MonthlyTrendDto {
    private int year;
    private int month;
    private BigDecimal totalAmount;
}
