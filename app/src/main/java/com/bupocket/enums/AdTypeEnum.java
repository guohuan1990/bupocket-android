package com.bupocket.enums;

public enum AdTypeEnum {
    BUY(0,"buy"),
    SELL(1,"sell")
    ;
    private Integer code;
    private String name;

    private AdTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Integer getCode() {
        return code;
    }
}
