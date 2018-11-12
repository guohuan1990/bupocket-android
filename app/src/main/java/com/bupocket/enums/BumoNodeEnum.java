package com.bupocket.enums;

public enum BumoNodeEnum {
    TEST(0,"BUMO_TEST_NODE_URL"),
    RELEASE(1,"BUMO_NODE_URL")
    ;
    private int code;
    private String url;

    private BumoNodeEnum(int code,String url) {
        this.code = code;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public int getCode() {
        return code;
    }
}
