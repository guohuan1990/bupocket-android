package com.bupocket.http.api.dto.resp;

import java.util.List;

public class SearchTokenRespDto {

    /**
     * data : {"page":{"count":1,"curSize":1,"endOfGroup":1,"firstResultNumber":0,"nextFlag":false,"queryTotal":true,"size":100,"start":1,"startOfGroup":1,"total":1},"tokenList":[{"assetCode":"FMC","icon":"","issuer":"buQr3aSDMLFbcNghgEp5LyHJVLJzkwZmm3bz","type":1}]}
     * errCode : 0
     * msg : 成功
     */

    private DataBean data;
    private String errCode;
    private String msg;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean {
        /**
         * page : {"count":1,"curSize":1,"endOfGroup":1,"firstResultNumber":0,"nextFlag":false,"queryTotal":true,"size":100,"start":1,"startOfGroup":1,"total":1}
         * tokenList : [{"assetCode":"FMC","icon":"","issuer":"buQr3aSDMLFbcNghgEp5LyHJVLJzkwZmm3bz","type":1}]
         */

        private PageBean page;
        private List<TokenListBean> tokenList;

        public PageBean getPage() {
            return page;
        }

        public void setPage(PageBean page) {
            this.page = page;
        }

        public List<TokenListBean> getTokenList() {
            return tokenList;
        }

        public void setTokenList(List<TokenListBean> tokenList) {
            this.tokenList = tokenList;
        }

        public static class PageBean {
            /**
             * count : 1
             * curSize : 1
             * endOfGroup : 1
             * firstResultNumber : 0
             * nextFlag : false
             * queryTotal : true
             * size : 100
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
}
