package com.bupocket.http.api.dto.resp;

import com.google.gson.Gson;

import java.util.List;

public class GetCardDetailsDto {

    /**
     * AssetInfo : {"name":"牛肉代金券","code":"RNC-1000","issuerAddress":"buQZf3Uz8HzjCtZBBwK9ce9gkbj9G4Ew4grT","issuerName":"现牛羊","issuerLogo":"base64","myAssetQty":"3"}
     * mySale : [{"adTitle":"如康牛腩块1kg生牛肉 整肉原切生鲜 生鲜 清真食品咖哩牛肉 牛腩肉","price":"60","saleTotal":"10","selledAmount":"5"}]
     * buyRequest : [{"adTitle":"如康牛腩块1kg生牛肉 整肉原切生鲜 生鲜 清真食品咖哩牛肉 牛腩肉","price":"60","adId":"10000000","issuer":{"name":"现牛羊","logo":"base64"}}]
     */

    private AssetInfoBean assetInfo;
    private List<MySaleBean> mySale;
    private List<BuyRequestBean> buyRequest;

    public static GetCardDetailsDto objectFromData(String str) {

        return new Gson().fromJson(str, GetCardDetailsDto.class);
    }

    public AssetInfoBean getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfoBean assetInfo) {
        this.assetInfo = assetInfo;
    }

    public List<MySaleBean> getMySale() {
        return mySale;
    }

    public void setMySale(List<MySaleBean> mySale) {
        this.mySale = mySale;
    }

    public List<BuyRequestBean> getBuyRequest() {
        return buyRequest;
    }

    public void setBuyRequest(List<BuyRequestBean> buyRequest) {
        this.buyRequest = buyRequest;
    }

    public static class AssetInfoBean {
        /**
         * name : 牛肉代金券
         * code : RNC-1000
         * issuerAddress : buQZf3Uz8HzjCtZBBwK9ce9gkbj9G4Ew4grT
         * issuerName : 现牛羊
         * issuerLogo : base64
         * myAssetQty : 3
         */

        private String name;
        private String code;
        private String issuerAddress;
        private String issuerName;
        private String issuerLogo;
        private String myAssetQty;

        public static AssetInfoBean objectFromData(String str) {

            return new Gson().fromJson(str, AssetInfoBean.class);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getIssuerAddress() {
            return issuerAddress;
        }

        public void setIssuerAddress(String issuerAddress) {
            this.issuerAddress = issuerAddress;
        }

        public String getIssuerName() {
            return issuerName;
        }

        public void setIssuerName(String issuerName) {
            this.issuerName = issuerName;
        }

        public String getIssuerLogo() {
            return issuerLogo;
        }

        public void setIssuerLogo(String issuerLogo) {
            this.issuerLogo = issuerLogo;
        }

        public String getMyAssetQty() {
            return myAssetQty;
        }

        public void setMyAssetQty(String myAssetQty) {
            this.myAssetQty = myAssetQty;
        }
    }

    public static class MySaleBean {
        /**
         * adTitle : 如康牛腩块1kg生牛肉 整肉原切生鲜 生鲜 清真食品咖哩牛肉 牛腩肉
         * price : 60
         * saleTotal : 10
         * selledAmount : 5
         */

        private String adTitle;
        private String price;
        private String saleTotal;
        private String selledAmount;

        public static MySaleBean objectFromData(String str) {

            return new Gson().fromJson(str, MySaleBean.class);
        }

        public String getAdTitle() {
            return adTitle;
        }

        public void setAdTitle(String adTitle) {
            this.adTitle = adTitle;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getSaleTotal() {
            return saleTotal;
        }

        public void setSaleTotal(String saleTotal) {
            this.saleTotal = saleTotal;
        }

        public String getSelledAmount() {
            return selledAmount;
        }

        public void setSelledAmount(String selledAmount) {
            this.selledAmount = selledAmount;
        }
    }

    public static class BuyRequestBean {
        /**
         * adTitle : 如康牛腩块1kg生牛肉 整肉原切生鲜 生鲜 清真食品咖哩牛肉 牛腩肉
         * price : 60
         * adId : 10000000
         * issuer : {"name":"现牛羊","logo":"base64"}
         */

        private String adTitle;
        private String price;
        private String adId;
        private IssuerBean issuer;

        public static BuyRequestBean objectFromData(String str) {

            return new Gson().fromJson(str, BuyRequestBean.class);
        }

        public String getAdTitle() {
            return adTitle;
        }

        public void setAdTitle(String adTitle) {
            this.adTitle = adTitle;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getAdId() {
            return adId;
        }

        public void setAdId(String adId) {
            this.adId = adId;
        }

        public IssuerBean getIssuer() {
            return issuer;
        }

        public void setIssuer(IssuerBean issuer) {
            this.issuer = issuer;
        }

        public static class IssuerBean {
            /**
             * name : 现牛羊
             * logo : base64
             */

            private String name;
            private String logo;

            public static IssuerBean objectFromData(String str) {

                return new Gson().fromJson(str, IssuerBean.class);
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getLogo() {
                return logo;
            }

            public void setLogo(String logo) {
                this.logo = logo;
            }
        }
    }
}
