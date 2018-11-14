package com.bupocket.enums;

public enum BumoNodeEnum {
    TEST(0,"TEST"),
    MAIN(1,"MAIN")
    ;
    private int code;
    private String name;

    private BumoNodeEnum(int code,String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }
}
