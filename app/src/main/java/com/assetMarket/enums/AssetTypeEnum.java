package com.assetMarket.enums;

public enum AssetTypeEnum {
	
	ATP_FIXED("1", "ATP固定型"),
	ATP_ADD("2", "ATP增发型"),
	ATP_INFINITE("3", "ATP无限型"),
	CTP_FIXED("4", "CTP固定型"),
    ;
	
    private String code;
    private String msg;

    private AssetTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

	public String getMsg() {
		return msg;
	}
    
}
