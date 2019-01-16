package com.bupocket.model;

import com.google.gson.Gson;

public class currentIdentity {

    /**
     * randomNumber : 通过keystore存储的随机数
     * identityName : 身份名
     * identityAddress : 身份账户地址
     * identityKeyStore : 通过身份私钥和密码生成的keystore
     * walletName : 钱包名
     * walletAddress : 钱包账户地址
     * walletKeyStore : 通过钱包私钥和密码生成的keystore
     */

    private String randomNumber;
    private String identityName;
    private String identityAddress;
    private String identityKeyStore;
    private String walletName;
    private String walletAddress;
    private String walletKeyStore;

    public static currentIdentity objectFromData(String str) {

        return new Gson().fromJson(str, currentIdentity.class);
    }

    public String getRandomNumber() {
        return randomNumber;
    }

    public void setRandomNumber(String randomNumber) {
        this.randomNumber = randomNumber;
    }

    public String getIdentityName() {
        return identityName;
    }

    public void setIdentityName(String identityName) {
        this.identityName = identityName;
    }

    public String getIdentityAddress() {
        return identityAddress;
    }

    public void setIdentityAddress(String identityAddress) {
        this.identityAddress = identityAddress;
    }

    public String getIdentityKeyStore() {
        return identityKeyStore;
    }

    public void setIdentityKeyStore(String identityKeyStore) {
        this.identityKeyStore = identityKeyStore;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getWalletKeyStore() {
        return walletKeyStore;
    }

    public void setWalletKeyStore(String walletKeyStore) {
        this.walletKeyStore = walletKeyStore;
    }
}
