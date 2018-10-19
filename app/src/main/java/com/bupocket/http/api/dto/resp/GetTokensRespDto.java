package com.bupocket.http.api.dto.resp;

import java.util.List;

public class GetTokensRespDto {

    private String currencyType;
    private String totalAmount;
    private List<TokenListBean> tokenList;

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<TokenListBean> getTokenList() {
        return tokenList;
    }

    public void setTokenList(List<TokenListBean> tokenList) {
        this.tokenList = tokenList;
    }

    public static class TokenListBean {

        private String amount;
        private String assetAmount;
        private String assetCode;
        private int decimals;
        private String icon;
        private String issuer;
        private String price;
        private int type;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getAssetCode() {
            return assetCode;
        }

        public void setAssetCode(String assetCode) {
            this.assetCode = assetCode;
        }

        public int getDecimals() {
            return decimals;
        }

        public void setDecimals(int decimals) {
            this.decimals = decimals;
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

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getAssetAmount() {
            return assetAmount;
        }

        public void setAssetAmount(String assetAmount) {
            this.assetAmount = assetAmount;
        }
    }
}
