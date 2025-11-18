package com.budgie.server.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompareExpenseDto {
    private Long current; //최근
    private Long previous; //이전
    private Long difference; //차이
    private  Double percent; //변화율
}
