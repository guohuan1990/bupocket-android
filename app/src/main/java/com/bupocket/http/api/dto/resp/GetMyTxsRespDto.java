package com.bupocket.http.api.dto.resp;

import java.util.List;

public class GetMyTxsRespDto{


    /**
     * page : {"count":1,"curSize":2,"endOfGroup":1,"firstResultNumber":0,"nextFlag":false,"queryTotal":true,"size":10,"start":1,"startOfGroup":1,"total":2}
     * txRecord : [{"amount":"45","amountUnit":0,"fromAddress":"buQswSaKDACkrFsnP1wcVsLAUzXQsemauEjf","ledgerSeq":1172893,"outinType":1,"toAddress":"buQg4XPL35gxkDxyNmrjH6GrecSpdzzW6vxv","txHash":"64fc36224d1c31962f662cb5f4f8fc4b06798decbf7d589d9d4bfb82bb69dbf5","txStatus":0,"txTime":1536810617193169},{"amount":"25","amountUnit":0,"fromAddress":"buQswSaKDACkrFsnP1wcVsLAUzXQsemauEjf","ledgerSeq":1168565,"outinType":1,"toAddress":"buQg4XPL35gxkDxyNmrjH6GrecSpdzzW6vxv","txHash":"74e85bbfa1def09949b4d5aea1867e7d2af57b81d89881f34a6aa8c085461bb3","txStatus":0,"txTime":1536762146705924}]
     */

    private PageBean page;
    private List<TxRecordBean> txRecord;

    public PageBean getPage() {
        return page;
    }

    public void setPage(PageBean page) {
        this.page = page;
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

    public static class TxRecordBean {
        /**
         * amount : 45
         * amountUnit : 0
         * fromAddress : buQswSaKDACkrFsnP1wcVsLAUzXQsemauEjf
         * ledgerSeq : 1172893
         * outinType : 1
         * toAddress : buQg4XPL35gxkDxyNmrjH6GrecSpdzzW6vxv
         * txHash : 64fc36224d1c31962f662cb5f4f8fc4b06798decbf7d589d9d4bfb82bb69dbf5
         * txStatus : 0
         * txTime : 1536810617193169
         */

        private String amount;
        private int amountUnit;
        private String fromAddress;
        private int ledgerSeq;
        private int outinType;
        private String toAddress;
        private String txHash;
        private int txStatus;
        private long txTime;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public int getAmountUnit() {
            return amountUnit;
        }

        public void setAmountUnit(int amountUnit) {
            this.amountUnit = amountUnit;
        }

        public String getFromAddress() {
            return fromAddress;
        }

        public void setFromAddress(String fromAddress) {
            this.fromAddress = fromAddress;
        }

        public int getLedgerSeq() {
            return ledgerSeq;
        }

        public void setLedgerSeq(int ledgerSeq) {
            this.ledgerSeq = ledgerSeq;
        }

        public int getOutinType() {
            return outinType;
        }

        public void setOutinType(int outinType) {
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
