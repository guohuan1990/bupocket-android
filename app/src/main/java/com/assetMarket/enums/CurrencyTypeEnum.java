package com.assetMarket.enums;

public enum CurrencyTypeEnum {
    CNY(0,"CNY","¥"),
    USD(1,"USD","$"),
    JPY(2,"JPY","¥"),
    KRW(3,"KRW","₩")
    ;

    private Integer code;
    private String name;
    private String symbol;

    private CurrencyTypeEnum(Integer code,String name,String symbol) {
        this.code = code;
        this.name = name;
        this.symbol = symbol;
    }


    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }
}
