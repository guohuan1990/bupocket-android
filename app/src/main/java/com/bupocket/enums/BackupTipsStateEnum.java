package com.bupocket.enums;

public enum BackupTipsStateEnum {
    SHOW("0","显示"),
    HIDE("1","隐藏")
    ;

    private String code;
    private String name;

    private BackupTipsStateEnum(String code,String name){
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
