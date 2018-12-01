package com.bupocket.http.api.dto.resp;

import java.util.List;

public class GetCardAdDatasRespDto {

    /**
     * advertList : [{"advertId":"10000020","advertTitle":"阳澄湖牌大闸蟹礼券 8只装","price":"2","coin":"BU","stockQuantity":"5","issuer":{"name":"现牛羊","photo":"base64"}}]
     * page : {"count":1,"curSize":2,"endOfGroup":1,"firstResultNumber":0,"nextFlag":false,"queryTotal":true,"size":10,"start":1,"startOfGroup":1,"total":2}
     */

    private PageBean page;
    private List<AdvertListBean> advertList;

    public PageBean getPage() {
        return page;
    }

    public void setPage(PageBean page) {
        this.page = page;
    }

    public List<AdvertListBean> getAdvertList() {
        return advertList;
    }

    public void setAdvertList(List<AdvertListBean> advertList) {
        this.advertList = advertList;
    }

    public static class PageBean {
        /**
         * count : 1
         * curSize : 2
         * endOfGroup : 1
         * firstResultNumber : 0
         * nextFlag : false
         * queryTotal : true
         * size : 10
         * start : 1
         * startOfGroup : 1
         * total : 2
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

    public static class AdvertListBean {
        /**
         * advertId : 10000020
         * advertTitle : 阳澄湖牌大闸蟹礼券 8只装
         * price : 2
         * coin : BU
         * stockQuantity : 5
         * issuer : {"name":"现牛羊","photo":"base64"}
         */

        private String advertId;
        private String advertTitle;
        private String price;
        private String coin;
        private String stockQuantity;
        private IssuerBean issuer;

        public String getAdvertId() {
            return advertId;
        }

        public void setAdvertId(String advertId) {
            this.advertId = advertId;
        }

        public String getAdvertTitle() {
            return advertTitle;
        }

        public void setAdvertTitle(String advertTitle) {
            this.advertTitle = advertTitle;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getCoin() {
            return coin;
        }

        public void setCoin(String coin) {
            this.coin = coin;
        }

        public String getStockQuantity() {
            return stockQuantity;
        }

        public void setStockQuantity(String stockQuantity) {
            this.stockQuantity = stockQuantity;
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
             * photo : base64
             */

            private String name;
            private String photo;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPhoto() {
                return photo;
            }

            public void setPhoto(String photo) {
                this.photo = photo;
            }
        }
    }
}
