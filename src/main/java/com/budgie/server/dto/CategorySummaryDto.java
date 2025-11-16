package com.budgie.server.dto;

import com.budgie.server.enums.CategoryName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CategorySummaryDto {
    private CategoryName categoryName;
    private BigDecimal totalAmount;
}
