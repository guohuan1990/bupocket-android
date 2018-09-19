package com.bupocket.enums;

public enum OutinTypeEnum {
    OUT(1,"out"),
    IN(0,"in")
    ;
    private Integer code;
    private String name;

    private OutinTypeEnum(Integer code,String name) {
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
