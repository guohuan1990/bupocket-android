package com.assetMarket.enums;

public enum TokenTypeEnum {
    BU("0","BU"),
    ATP10("1","ATP10"),
    CTP10("2","CTP10")
    ;
    private String code;
    private String name;

    private TokenTypeEnum(String code,String name) {
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
