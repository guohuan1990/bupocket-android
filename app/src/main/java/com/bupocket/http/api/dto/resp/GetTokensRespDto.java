package com.bupocket.http.api.dto.resp;

import com.bupocket.model.TokenInfo;

import java.util.List;

public class GetTokensRespDto {

    private String totalAssets;
    private List<TokenInfo> tokens;

    public List<TokenInfo> getTokens() {
        return tokens;
    }

    public void setTokens(List<TokenInfo> tokens) {
        this.tokens = tokens;
    }

    public String getTotalAssets() {
        return totalAssets;
    }

    public void setTotalAssets(String totalAssets) {
        this.totalAssets = totalAssets;
    }
}
