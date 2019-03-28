package com.bupocket.http.api.dto.resp;

public class UserScanQrLoginDto {
    private String uuid;
    private String appId;
    private String appName;
    private String appPic;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppPic() {
        return appPic;
    }

    public void setAppPic(String appPic) {
        this.appPic = appPic;
    }
}
