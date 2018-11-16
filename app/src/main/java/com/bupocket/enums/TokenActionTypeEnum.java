package com.bupocket.enums;

public enum TokenActionTypeEnum {
    ISSUE("token.issue","发行资产"),
    REGISTER("token.register","登记资产")
    ;
    private String code;
    private String name;

    private TokenActionTypeEnum(String code,String name) {
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
