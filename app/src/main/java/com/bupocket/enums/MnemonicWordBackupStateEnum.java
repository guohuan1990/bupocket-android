package com.bupocket.enums;

public enum MnemonicWordBackupStateEnum {
    ALREADYBACKUP("0","已备份"),
    NOBACKUP("1","关闭")
    ;

    private String code;
    private String name;

    private MnemonicWordBackupStateEnum(String code,String name){
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
