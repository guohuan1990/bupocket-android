package com.bupocket.model;

public class TokenTxInfo {
    private String txAccountAddress;
    private String txDate;
    private String txAmount;
    private String txStatus;

    public TokenTxInfo(String txAccountAddress, String txDate, String txAmount, String txStatus) {
        this.txAccountAddress = txAccountAddress;
        this.txDate = txDate;
        this.txAmount = txAmount;
        this.txStatus = txStatus;
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
}
