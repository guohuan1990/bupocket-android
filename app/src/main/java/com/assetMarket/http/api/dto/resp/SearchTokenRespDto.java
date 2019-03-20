package com.assetMarket.http.api.dto.resp;

import java.util.List;

public class SearchTokenRespDto {


    private List<TokenListBean> tokenList;

    public List<TokenListBean> getTokenList() {
        return tokenList;
    }

    public void setTokenList(List<TokenListBean> tokenList) {
        this.tokenList = tokenList;
    }

    public static class TokenListBean {
        /**
         * assetCode : FMC
         * icon :
         * issuer : buQr3aSDMLFbcNghgEp5LyHJVLJzkwZmm3bz
         * type : 1
         */

        private String assetCode;
        private String assetName;
        private String icon;
        private String issuer;
        private int recommend;
        private int type;

        public String getAssetCode() {
            return assetCode;
        }

        public void setAssetCode(String assetCode) {
            this.assetCode = assetCode;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getAssetName() {
            return assetName;
        }

        public void setAssetName(String assetName) {
            this.assetName = assetName;
        }

        public int getRecommend() {
            return recommend;
        }

        public void setRecommend(int recommend) {
            this.recommend = recommend;
        }
    }
}
