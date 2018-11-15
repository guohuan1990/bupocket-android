package com.bupocket.enums;

public enum BumoNodeEnum {
    TEST(0,"TEST","myTestNetTokenList"),
    MAIN(1,"MAIN","myMainNetTokenList")
    ;
    private int code;
    private String name;
    private String localTokenListSharedPreferenceKey;

    private BumoNodeEnum(int code,String name,String localTokenListSharedPreferenceKey) {
        this.code = code;
        this.name = name;
        this.localTokenListSharedPreferenceKey = localTokenListSharedPreferenceKey;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public String getLocalTokenListSharedPreferenceKey() {
        return localTokenListSharedPreferenceKey;
    }
}
