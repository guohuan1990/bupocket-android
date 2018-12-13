package com.bupocket.enums;

public enum LanguageEnum {
    CHINESE(0,"zh","简体中文"),
    ENGLISH(1,"en","English"),
    UNDEFINED(-1,"undefined","Undefined")
    ;
    private int id;
    private String code;
    private String name;

    private LanguageEnum(int id,String code,String name){
        this.id = id;
        this.code = code;
        this.name = name;
    }


    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
