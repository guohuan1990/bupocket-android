package com.bupocket.enums;

public enum ExceptionEnum {
    SUCCESS("0", "success"),
    FAIL("1","系统内部错误"),
    USER_TOKEN_ERR("100006","用户令牌已失效"),
    ;

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
