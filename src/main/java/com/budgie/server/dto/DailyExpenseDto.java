package com.budgie.server.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyExpenseDto {
    private int day;
    private BigDecimal totalAmount;
}
