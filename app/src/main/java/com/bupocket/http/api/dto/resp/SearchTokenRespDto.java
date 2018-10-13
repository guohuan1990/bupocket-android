package com.bupocket.http.api.dto.resp;

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
        private String icon;
        private String issuer;
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
    }
}
