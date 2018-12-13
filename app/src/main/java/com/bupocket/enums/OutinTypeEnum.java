package com.bupocket.enums;

public enum OutinTypeEnum {
    OUT("0","out"),
    IN("1","in")
    ;
    private String code;
    private String name;

    private OutinTypeEnum(String code,String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
