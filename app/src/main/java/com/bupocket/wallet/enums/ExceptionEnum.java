package com.bupocket.wallet.enums;

public enum ExceptionEnum {
    SYS_ERR("10000","system exception"),
    INVALID_PASSWORD("10001","Invalid password"),
    FEE_NOT_ENOUGH("10002","Fee not enough"),
    BU_NOT_ENOUGH("10003","bu not enough");

    private final String code;
    private final String message;

    private ExceptionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
}
