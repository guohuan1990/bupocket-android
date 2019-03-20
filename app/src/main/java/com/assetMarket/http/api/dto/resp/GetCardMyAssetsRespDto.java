package com.assetMarket.http.api.dto.resp;

import com.google.gson.Gson;

import java.util.List;

public class GetCardMyAssetsRespDto {

    /**
     * myAssets : [{"issuer":{"name":"现牛羊","address":"buQZf3Uz8HzjCtZBBwK9ce9gkbj9G4Ew4grT","logo":"base64"},"AssetInfo":{"name":"牛肉代金券","code":"RNC-1000","issuerAddress":"buQZf3Uz8HzjCtZBBwK9ce9gkbj9G4Ew4grT","myAssetQty":"3"}}]
     * page : {"count":1,"curSize":1,"endOfGroup":1,"firstResultNumber":0,"nextFlag":false,"queryTotal":true,"size":10,"start":1,"startOfGroup":1,"total":1}
     */

    private PageBean page;
    private List<MyAssetsBean> myAssets;

    public static GetCardMyAssetsRespDto objectFromData(String str) {

        return new Gson().fromJson(str, GetCardMyAssetsRespDto.class);
    }

    public PageBean getPage() {
        return page;
    }

    public void setPage(PageBean page) {
        this.page = page;
    }

    public List<MyAssetsBean> getMyAssets() {
        return myAssets;
    }

    public void setMyAssets(List<MyAssetsBean> myAssets) {
        this.myAssets = myAssets;
    }

    public static class PageBean {
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

        public static PageBean objectFromData(String str) {

            return new Gson().fromJson(str, PageBean.class);
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

    public static class MyAssetsBean {
        /**
         * issuer : {"name":"现牛羊","address":"buQZf3Uz8HzjCtZBBwK9ce9gkbj9G4Ew4grT","logo":"base64"}
         * AssetInfo : {"name":"牛肉代金券","code":"RNC-1000","issuerAddress":"buQZf3Uz8HzjCtZBBwK9ce9gkbj9G4Ew4grT","myAssetQty":"3"}
         */

        private IssuerBean issuer;
        private AssetInfoBean assetInfo;

        public static MyAssetsBean objectFromData(String str) {

            return new Gson().fromJson(str, MyAssetsBean.class);
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
            private String abbreviation;

            public String getAbbreviation() {
                return abbreviation;
            }

            public void setAbbreviation(String abbreviation) {
                this.abbreviation = abbreviation;
            }

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
}
