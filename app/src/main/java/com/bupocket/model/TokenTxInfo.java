package com.bupocket.model;

public class TokenTxInfo {
    private String txAccountAddress;
    private String txDate;
    private String txAmount;
    private String txStatus;
    private String outinType;
    private String txHash;
    private String optNo;

    public TokenTxInfo(String txAccountAddress, String txDate, String txAmount, String txStatus,String optNo) {
        this.txAccountAddress = txAccountAddress;
        this.txDate = txDate;
        this.txAmount = txAmount;
        this.txStatus = txStatus;
        this.optNo = optNo;
    }

    public String getTxAccountAddress() {
        return txAccountAddress;
    }

    public void setTxAccountAddress(String txAccountAddress) {
        this.txAccountAddress = txAccountAddress;
    }

    public String getTxDate() {
        return txDate;
    }

    public void setTxDate(String txDate) {
        this.txDate = txDate;
    }

    public String getTxAmount() {
        return txAmount;
    }

    public void setTxAmount(String txAmount) {
        this.txAmount = txAmount;
    }

    public String getTxStatus() {
        return txStatus;
    }

    public void setTxStatus(String txStatus) {
        this.txStatus = txStatus;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getOutinType() {
        return outinType;
    }

    public void setOutinType(String outinType) {
        this.outinType = outinType;
    }

    public String getOptNo() {
        return optNo;
    }

    public void setOptNo(String optNo) {
        this.optNo = optNo;
    }
}
