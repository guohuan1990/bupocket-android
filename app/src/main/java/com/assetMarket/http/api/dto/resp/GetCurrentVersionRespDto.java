package com.assetMarket.http.api.dto.resp;

public class GetCurrentVersionRespDto {

    private String appSize;
    private String createTime;
    private String downloadLink;
    private String englishVerContents;
    private String updateTime;
    private String verContents;
    private String verNumber;
    private String verNumberCode;
    private int verType;

    public String getAppSize() {
        return appSize;
    }

    public void setAppSize(String appSize) {
        this.appSize = appSize;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public String getEnglishVerContents() {
        return englishVerContents;
    }

    public void setEnglishVerContents(String englishVerContents) {
        this.englishVerContents = englishVerContents;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getVerContents() {
        return verContents;
    }

    public void setVerContents(String verContents) {
        this.verContents = verContents;
    }

    public String getVerNumber() {
        return verNumber;
    }

    public void setVerNumber(String verNumber) {
        this.verNumber = verNumber;
    }

    public String getVerNumberCode() {
        return verNumberCode;
    }

    public void setVerNumberCode(String verNumberCode) {
        this.verNumberCode = verNumberCode;
    }

    public int getVerType() {
        return verType;
    }

    public void setVerType(int verType) {
        this.verType = verType;
    }
}
