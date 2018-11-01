package com.bupocket.http.api.dto.resp;

import com.google.gson.Gson;

public class GetTokenDetailRespDto {

    /**
     * actualSupply : 0
     * assetCode : HAN
     * assetName : Demon Token HAN
     * decimals : 4
     * isOverFlow : 0
     * issueAddress : buQWESXjdgXSFFajEZfkwi5H4fuAyTGgzkje
     * tokenDescription : This is HAN Token
     * tokenType : 2
     * totalSupply : 10000000000000000
     * version : 1.0
     */

    private String actualSupply;
    private String assetCode;
    private String assetName;
    private String decimals;
    private int isOverFlow;
    private String issueAddress;
    private String tokenDescription;
    private String tokenType;
    private String totalSupply;
    private String version;

    public static GetTokenDetailRespDto objectFromData(String str) {

        return new Gson().fromJson(str, GetTokenDetailRespDto.class);
    }

    public String getActualSupply() {
        return actualSupply;
    }

    public void setActualSupply(String actualSupply) {
        this.actualSupply = actualSupply;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getDecimals() {
        return decimals;
    }

    public void setDecimals(String decimals) {
        this.decimals = decimals;
    }

    public int getIsOverFlow() {
        return isOverFlow;
    }

    public void setIsOverFlow(int isOverFlow) {
        this.isOverFlow = isOverFlow;
    }

    public String getIssueAddress() {
        return issueAddress;
    }

    public void setIssueAddress(String issueAddress) {
        this.issueAddress = issueAddress;
    }

    public String getTokenDescription() {
        return tokenDescription;
    }

    public void setTokenDescription(String tokenDescription) {
        this.tokenDescription = tokenDescription;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getTotalSupply() {
        return totalSupply;
    }

    public void setTotalSupply(String totalSupply) {
        this.totalSupply = totalSupply;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
