package com.assetMarket.http.api.dto.resp;

import com.google.gson.Gson;

import java.util.List;

public class GetMyTxsRespDto{

    /**
     * page : {"count":1,"curSize":1,"endOfGroup":1,"firstResultNumber":0,"nextFlag":false,"queryTotal":true,"size":10,"start":1,"startOfGroup":1,"total":1}
     * assetData : {"totalAmount":"~","currencyType":"CNY","price":"~","balance":"10.25"}
     * txRecord : [{"amount":"20","fromAddress":"buQWESXjdgXSFFajEZfkwi5H4fuAyTGgzkje","ledger":"1621644","optNo":32,"outinType":"0","toAddress":"buQXqF35Pa8duB1oUGBhNSZBLdMmc93LzrGt","txHash":"4dac7c8b55f7b9784d70d89cbf89f03c26b5c770c6683be8d4cc935589c26d92","txStatus":1,"txTime":1541406573582965}]
     */

    private PageBean page;
    private AssetDataBean assetData;
    private List<TxRecordBean> txRecord;

    public static GetMyTxsRespDto objectFromData(String str) {

        return new Gson().fromJson(str, GetMyTxsRespDto.class);
    }

    public PageBean getPage() {
        return page;
    }

    public void setPage(PageBean page) {
        this.page = page;
    }

    public AssetDataBean getAssetData() {
        return assetData;
    }

    public void setAssetData(AssetDataBean assetData) {
        this.assetData = assetData;
    }

    public List<TxRecordBean> getTxRecord() {
        return txRecord;
    }

    public void setTxRecord(List<TxRecordBean> txRecord) {
        this.txRecord = txRecord;
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

    public static class AssetDataBean {
        /**
         * totalAmount : ~
         * currencyType : CNY
         * price : ~
         * balance : 10.25
         */

        private String totalAmount;
        private String currencyType;
        private String price;
        private String balance;

        public static AssetDataBean objectFromData(String str) {

            return new Gson().fromJson(str, AssetDataBean.class);
        }

        public String getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(String totalAmount) {
            this.totalAmount = totalAmount;
        }

        public String getCurrencyType() {
            return currencyType;
        }

        public void setCurrencyType(String currencyType) {
            this.currencyType = currencyType;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }
    }

    public static class TxRecordBean {
        /**
         * amount : 20
         * fromAddress : buQWESXjdgXSFFajEZfkwi5H4fuAyTGgzkje
         * ledger : 1621644
         * optNo : 32
         * outinType : 0
         * toAddress : buQXqF35Pa8duB1oUGBhNSZBLdMmc93LzrGt
         * txHash : 4dac7c8b55f7b9784d70d89cbf89f03c26b5c770c6683be8d4cc935589c26d92
         * txStatus : 1
         * txTime : 1541406573582965
         */

        private String amount;
        private String fromAddress;
        private String ledger;
        private long optNo;
        private String outinType;
        private String toAddress;
        private String txHash;
        private int txStatus;
        private long txTime;

        public static TxRecordBean objectFromData(String str) {

            return new Gson().fromJson(str, TxRecordBean.class);
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getFromAddress() {
            return fromAddress;
        }

        public void setFromAddress(String fromAddress) {
            this.fromAddress = fromAddress;
        }

        public String getLedger() {
            return ledger;
        }

        public void setLedger(String ledger) {
            this.ledger = ledger;
        }

        public long getOptNo() {
            return optNo;
        }

        public void setOptNo(long optNo) {
            this.optNo = optNo;
        }

        public String getOutinType() {
            return outinType;
        }

        public void setOutinType(String outinType) {
            this.outinType = outinType;
        }

        public String getToAddress() {
            return toAddress;
        }

        public void setToAddress(String toAddress) {
            this.toAddress = toAddress;
        }

        public String getTxHash() {
            return txHash;
        }

        public void setTxHash(String txHash) {
            this.txHash = txHash;
        }

        public int getTxStatus() {
            return txStatus;
        }

        public void setTxStatus(int txStatus) {
            this.txStatus = txStatus;
        }

        public long getTxTime() {
            return txTime;
        }

        public void setTxTime(long txTime) {
            this.txTime = txTime;
        }
    }
}
