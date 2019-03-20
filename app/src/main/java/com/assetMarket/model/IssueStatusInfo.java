package com.assetMarket.model;

import com.google.gson.Gson;

public class IssueStatusInfo {

    /**
     * errorCode : 0
     * errorMsg :
     * data : {"name":"资产名称","code":"资产代码","type":"登记方式","total":"登记资产总量","decimals":"资产精度","version":"ATP版本","desc":"资产描述","fee":"实际交易费用","hash":"哈希","address":"发行方地址","issueTotal":"资产发行量"}
     */

    private int errorCode;
    private String errorMsg;
    private DataBean data;

    public static IssueStatusInfo objectFromData(String str) {

        return new Gson().fromJson(str, IssueStatusInfo.class);
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * name : 资产名称
         * code : 资产代码
         * type : 登记方式
         * total : 登记资产总量
         * decimals : 资产精度
         * version : ATP版本
         * desc : 资产描述
         * fee : 实际交易费用
         * hash : 哈希
         * address : 发行方地址
         * issueTotal : 资产发行量
         */

        private String name;
        private String code;
//        private String type;
        private String total;
        private String decimals;
        private String version;
        private String desc;
        private String fee;
        private String hash;
        private String address;
        private String issueTotal;

        public static DataBean objectFromData(String str) {

            return new Gson().fromJson(str, DataBean.class);
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

//        public String getType() {
//            return type;
//        }
//
//        public void setType(String type) {
//            this.type = type;
//        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getDecimals() {
            return decimals;
        }

        public void setDecimals(String decimals) {
            this.decimals = decimals;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getFee() {
            return fee;
        }

        public void setFee(String fee) {
            this.fee = fee;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getIssueTotal() {
            return issueTotal;
        }

        public void setIssueTotal(String issueTotal) {
            this.issueTotal = issueTotal;
        }
    }
}
