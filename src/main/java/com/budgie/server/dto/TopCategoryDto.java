package com.budgie.server.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopCategoryDto {
    private String categoryName;
    private Long totalAmount;
}
