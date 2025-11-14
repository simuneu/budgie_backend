package com.budgie.server.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetGoalDto {
    private Integer year;
    private Integer month;
    private Long goalAmount;
}
