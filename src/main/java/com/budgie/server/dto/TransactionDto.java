package com.budgie.server.dto;

import com.budgie.server.enums.BudgetType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {
    private Long transactionId;
    private String categoryName;
    private Long categoryId;
    private BudgetType budgetType;
    private BigDecimal amount;
    private String memo;
    private LocalDate transactionDate;
}
