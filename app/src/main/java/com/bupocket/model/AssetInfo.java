package com.bupocket.model;

import com.google.gson.Gson;

public class AssetInfo {

    /**
     * issuerAddress : buQZf3Uz8HzjCtZBBwK9ce9gkbj9G4Ew4grT
     * code : RNC-1000
     */

    private String issuerAddress;
    private String code;

    public static AssetInfo objectFromData(String str) {

        return new Gson().fromJson(str, AssetInfo.class);
    }

    public String getIssuerAddress() {
        return issuerAddress;
    }

    public void setIssuerAddress(String issuerAddress) {
        this.issuerAddress = issuerAddress;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
