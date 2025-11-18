package com.budgie.server.dto;

import com.budgie.server.entity.BudgetGoalEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalResponseDto {
    private Long userId;
    private int year;
    private int month;
    private Long  goalAmount;

    public static GoalResponseDto fromEntity(BudgetGoalEntity entity) {
        return GoalResponseDto.builder()
                .userId(entity.getUserId())
                .year(entity.getYear())
                .month(entity.getMonth())
                .goalAmount(entity.getGoalAmount())
                .build();
    }
}
