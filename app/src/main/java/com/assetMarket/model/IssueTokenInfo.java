package com.assetMarket.model;

import com.google.gson.Gson;

public class IssueTokenInfo {

    /**
     * code : token code
     * amount : token数量
     */

    private String code;
    private String amount;

    public static IssueTokenInfo objectFromData(String str) {

        return new Gson().fromJson(str, IssueTokenInfo.class);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
