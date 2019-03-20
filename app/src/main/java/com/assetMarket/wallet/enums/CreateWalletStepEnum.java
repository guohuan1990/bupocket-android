package com.assetMarket.wallet.enums;

public enum CreateWalletStepEnum {
    CREATE_MNEONIC_CODE("0","createMneonicCode"),
    BACKUPED_MNEONIC_CODE("1","backupedMneonicCode");

    private final String code;
    private final String description;

    private CreateWalletStepEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}