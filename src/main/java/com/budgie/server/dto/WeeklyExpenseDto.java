package com.budgie.server.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyExpenseDto {
    private int weekday;
    private BigDecimal totalAmount;
}
