package com.bupocket.enums;

public enum PaymentTypeEnum {
    AILPAY("alipay","支付宝"),
    WECHAT("wechat","微信"),
    BANKCARD("bank","银行卡")
    ;
    private String code;
    private String name;

    private PaymentTypeEnum(String code,String name) {
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
