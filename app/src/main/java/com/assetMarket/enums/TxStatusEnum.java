package com.assetMarket.enums;

public enum  TxStatusEnum {
    SUCCESS(0,"success"),
    FAIL(1,"FAIL")
    ;
    private Integer code;
    private String name;

    private TxStatusEnum(Integer code,String name) {
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
