package com.assetMarket.http.api.dto.resp;

import com.google.gson.Gson;

import java.util.List;

public class GetCardDetailsDto {

    /**
     * assetDetail : {"issuer":{"name":"现牛羊","address":"buQZf3Uz8HzjCtZBBwK9ce9gkbj9G4Ew4grT","logo":"base64"},"assetInfo":{"name":"牛肉代金券","code":"RNC-1000","issuerAddress":"buQZf3Uz8HzjCtZBBwK9ce9gkbj9G4Ew4grT","myAssetQty":"3"}}
     * mySale : [{"adTitle":"如康牛腩块1kg生牛肉 整肉原切生鲜 生鲜 清真食品咖哩牛肉 牛腩肉","price":"60","saleTotal":"10","selledAmount":"5"}]
     * buyRequest : [{"adTitle":"如康牛腩块1kg生牛肉 整肉原切生鲜 生鲜 清真食品咖哩牛肉 牛腩肉","price":"60","adId":"10000000","issuer":{"name":"现牛羊","logo":"base64"}}]
     * salePage : {"count":1,"curSize":1,"endOfGroup":1,"firstResultNumber":0,"nextFlag":false,"queryTotal":true,"size":10,"start":1,"startOfGroup":1,"total":1}
     * buyPage : {"count":1,"curSize":1,"endOfGroup":1,"firstResultNumber":0,"nextFlag":false,"queryTotal":true,"size":10,"start":1,"startOfGroup":1,"total":1}
     */

    private AssetDetailBean assetDetail;
    private SalePageBean salePage;
    private BuyPageBean buyPage;
    private List<MySaleBean> mySale;
    private List<BuyRequestBean> buyRequest;

    public static GetCardDetailsDto objectFromData(String str) {

        return new Gson().fromJson(str, GetCardDetailsDto.class);
    }

    public AssetDetailBean getAssetDetail() {
        return assetDetail;
    }

    public void setAssetDetail(AssetDetailBean assetDetail) {
        this.assetDetail = assetDetail;
    }

    public SalePageBean getSalePage() {
        return salePage;
    }

    public void setSalePage(SalePageBean salePage) {
        this.salePage = salePage;
    }

    public BuyPageBean getBuyPage() {
        return buyPage;
    }

    public void setBuyPage(BuyPageBean buyPage) {
        this.buyPage = buyPage;
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

    public static class AssetDetailBean {
        /**
         * issuer : {"name":"现牛羊","address":"buQZf3Uz8HzjCtZBBwK9ce9gkbj9G4Ew4grT","logo":"base64"}
         * assetInfo : {"name":"牛肉代金券","code":"RNC-1000","issuerAddress":"buQZf3Uz8HzjCtZBBwK9ce9gkbj9G4Ew4grT","myAssetQty":"3"}
         */

        private IssuerBean issuer;
        private AssetInfoBean assetInfo;

        public static AssetDetailBean objectFromData(String str) {

            return new Gson().fromJson(str, AssetDetailBean.class);
        }

        public IssuerBean getIssuer() {
            return issuer;
        }

        public void setIssuer(IssuerBean issuer) {
            this.issuer = issuer;
        }

        public AssetInfoBean getAssetInfo() {
            return assetInfo;
        }

        public void setAssetInfo(AssetInfoBean assetInfo) {
            this.assetInfo = assetInfo;
        }

        public static class IssuerBean {
            /**
             * name : 现牛羊
             * address : buQZf3Uz8HzjCtZBBwK9ce9gkbj9G4Ew4grT
             * logo : base64
             */

            private String name;
            private String address;
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

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getLogo() {
                return logo;
            }

            public void setLogo(String logo) {
                this.logo = logo;
            }
        }

        public static class AssetInfoBean {
            /**
             * name : 牛肉代金券
             * code : RNC-1000
             * issuerAddress : buQZf3Uz8HzjCtZBBwK9ce9gkbj9G4Ew4grT
             * myAssetQty : 3
             */

            private String name;
            private String code;
            private String issuerAddress;
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

            public String getMyAssetQty() {
                return myAssetQty;
            }

            public void setMyAssetQty(String myAssetQty) {
                this.myAssetQty = myAssetQty;
            }
        }
    }

    public static class SalePageBean {
        /**
         * count : 1
         * curSize : 1
         * endOfGroup : 1
         * firstResultNumber : 0
         * nextFlag : false
         * queryTotal : true
         * size : 10
         * start : 1
         * startOfGroup : 1
         * total : 1
         */

        private int count;
        private int curSize;
        private int endOfGroup;
        private int firstResultNumber;
        private boolean nextFlag;
        private boolean queryTotal;
        private int size;
        private int start;
        private int startOfGroup;
        private int total;

        public static SalePageBean objectFromData(String str) {

            return new Gson().fromJson(str, SalePageBean.class);
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getCurSize() {
            return curSize;
        }

        public void setCurSize(int curSize) {
            this.curSize = curSize;
        }

        public int getEndOfGroup() {
            return endOfGroup;
        }

        public void setEndOfGroup(int endOfGroup) {
            this.endOfGroup = endOfGroup;
        }

        public int getFirstResultNumber() {
            return firstResultNumber;
        }

        public void setFirstResultNumber(int firstResultNumber) {
            this.firstResultNumber = firstResultNumber;
        }

        public boolean isNextFlag() {
            return nextFlag;
        }

        public void setNextFlag(boolean nextFlag) {
            this.nextFlag = nextFlag;
        }

        public boolean isQueryTotal() {
            return queryTotal;
        }

        public void setQueryTotal(boolean queryTotal) {
            this.queryTotal = queryTotal;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getStartOfGroup() {
            return startOfGroup;
        }

        public void setStartOfGroup(int startOfGroup) {
            this.startOfGroup = startOfGroup;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }

    public static class BuyPageBean {
        /**
         * count : 1
         * curSize : 1
         * endOfGroup : 1
         * firstResultNumber : 0
         * nextFlag : false
         * queryTotal : true
         * size : 10
         * start : 1
         * startOfGroup : 1
         * total : 1
         */

        private int count;
        private int curSize;
        private int endOfGroup;
        private int firstResultNumber;
        private boolean nextFlag;
        private boolean queryTotal;
        private int size;
        private int start;
        private int startOfGroup;
        private int total;

        public static BuyPageBean objectFromData(String str) {

            return new Gson().fromJson(str, BuyPageBean.class);
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getCurSize() {
            return curSize;
        }

        public void setCurSize(int curSize) {
            this.curSize = curSize;
        }

        public int getEndOfGroup() {
            return endOfGroup;
        }

        public void setEndOfGroup(int endOfGroup) {
            this.endOfGroup = endOfGroup;
        }

        public int getFirstResultNumber() {
            return firstResultNumber;
        }

        public void setFirstResultNumber(int firstResultNumber) {
            this.firstResultNumber = firstResultNumber;
        }

        public boolean isNextFlag() {
            return nextFlag;
        }

        public void setNextFlag(boolean nextFlag) {
            this.nextFlag = nextFlag;
        }

        public boolean isQueryTotal() {
            return queryTotal;
        }

        public void setQueryTotal(boolean queryTotal) {
            this.queryTotal = queryTotal;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getStartOfGroup() {
            return startOfGroup;
        }

        public void setStartOfGroup(int startOfGroup) {
            this.startOfGroup = startOfGroup;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
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
        private IssuerBeanX issuer;

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

        public IssuerBeanX getIssuer() {
            return issuer;
        }

        public void setIssuer(IssuerBeanX issuer) {
            this.issuer = issuer;
        }

        public static class IssuerBeanX {
            /**
             * name : 现牛羊
             * logo : base64
             */

            private String name;
            private String logo;

            public static IssuerBeanX objectFromData(String str) {

                return new Gson().fromJson(str, IssuerBeanX.class);
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
