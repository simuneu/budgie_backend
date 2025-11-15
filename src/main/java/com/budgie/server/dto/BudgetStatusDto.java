package com.budgie.server.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetStatusDto {
    private Long goalAmount;
    private Long totalExpense; //총 소비액
    private Long remaining; //남은 금액 (목표-소비)
    private boolean isExceeded; //초과여부
}
