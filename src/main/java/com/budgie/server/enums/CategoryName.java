package com.budgie.server.enums;

public enum CategoryName {
    //지출
    FOOD("식비"),
    HOUSING("주거비"),
    TRANSPORT("교통"),
    CULTURE("문화생활"),
    LIVING("생활비"),
    HEALTH("건강"),
    MEDICAL("의료"),
    EDUCATION("교육"),
    EVENT("경조사비"),
    SAVINGS("예적금"),

    // 수입
    SALARY("월급"),
    ALLOWANCE("용돈"),
    SIDE_INCOME("부수입"),

    // 공통
    ETC("기타");


    //DB에는 영어, 프론트엔 한글 label
    private final String label;

    CategoryName(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
