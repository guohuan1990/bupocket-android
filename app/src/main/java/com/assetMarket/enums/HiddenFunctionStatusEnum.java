package com.assetMarket.enums;

public enum HiddenFunctionStatusEnum {
    ENABLE(0,"开启"),
    DISABLE(1,"关闭")
    ;

    private int code;
    private String name;

    private HiddenFunctionStatusEnum(int code,String name){
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
