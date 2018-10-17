package com.bupocket.model;

import com.google.gson.Gson;

import java.util.List;

public class MyTokens {

    private List<TokenListBean> tokenList;

    public static MyTokens objectFromData(String str) {

        return new Gson().fromJson(str, MyTokens.class);
    }

    public List<TokenListBean> getTokenList() {
        return tokenList;
    }

    public void setTokenList(List<TokenListBean> tokenList) {
        this.tokenList = tokenList;
    }

    public static class TokenListBean {
        /**
         * assetCode : CLB
         * issuer : buQWESXjdgXSFFajEZfkwi5H4fuAyTGgzkje
         */

        private String assetCode;
        private String issuer;

        public static TokenListBean objectFromData(String str) {

            return new Gson().fromJson(str, TokenListBean.class);
        }

        public String getAssetCode() {
            return assetCode;
        }

        public void setAssetCode(String assetCode) {
            this.assetCode = assetCode;
        }

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof TokenListBean){
                TokenListBean mnemonicWord = (TokenListBean)obj;
                return this.getAssetCode().equals(mnemonicWord.getAssetCode()) && this.getIssuer().equals(mnemonicWord.getIssuer());
            }
            return super.equals(obj);
        }
    }
}
