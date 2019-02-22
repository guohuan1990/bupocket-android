package com.bupocket.enums;

public enum AddressClickEventEnum {
    CHOOSE("0","CHOOSE"),
    EDIT("1","EDIT"),
    ;

    private String code;
    private String name;

    private AddressClickEventEnum(String code, String name){
        this.code = code;
        this.name = name;
    }


    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
