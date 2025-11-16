package com.budgie.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RecordedDayDto {
    private int day;
    private BigDecimal totalAmount;
}
