package com.bupocket.http.api.dto.resp;

import java.util.List;

public class GetCurrentVersionRespDto {


    private String createTime;
    private String downloadLink;
    private String updateTime;
    private String verNumber;
    private int verType;
    private List<String> englishVerContents;
    private List<String> verContents;

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

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getVerNumber() {
        return verNumber;
    }

    public void setVerNumber(String verNumber) {
        this.verNumber = verNumber;
    }

    public int getVerType() {
        return verType;
    }

    public void setVerType(int verType) {
        this.verType = verType;
    }

    public List<String> getEnglishVerContents() {
        return englishVerContents;
    }

    public void setEnglishVerContents(List<String> englishVerContents) {
        this.englishVerContents = englishVerContents;
    }

    public List<String> getVerContents() {
        return verContents;
    }

    public void setVerContents(List<String> verContents) {
        this.verContents = verContents;
    }
}
