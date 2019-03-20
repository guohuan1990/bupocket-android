package com.assetMarket.model;

public class AdInfo {
    private String userNick;
    private String transactionSize;
    private String soldValue;
    private String minValue;
    private String maxValue;
    private String amount;
    private String [] paymentTypeArr;

    public AdInfo(String userNick, String transactionSize, String soldValue, String minValue, String maxValue, String amount, String []paymentTypeArr) {
        this.userNick = userNick;
        this.transactionSize = transactionSize;
        this.soldValue = soldValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.amount = amount;
        this.paymentTypeArr = paymentTypeArr;
    }

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public String getTransactionSize() {
        return transactionSize;
    }

    public void setTransactionSize(String transactionSize) {
        this.transactionSize = transactionSize;
    }

    public String getSoldValue() {
        return soldValue;
    }

    public void setSoldValue(String soldValue) {
        this.soldValue = soldValue;
    }

    public String getMinValue() {
        return minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String[] getPaymentTypeArr() {
        return paymentTypeArr;
    }

    public void setPaymentTypeArr(String[] paymentTypeArr) {
        this.paymentTypeArr = paymentTypeArr;
    }
}
