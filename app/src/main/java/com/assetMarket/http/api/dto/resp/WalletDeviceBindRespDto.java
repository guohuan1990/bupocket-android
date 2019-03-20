package com.assetMarket.http.api.dto.resp;

public class WalletDeviceBindRespDto {

    /**
     * walletAddress : buQirJoV68Vv614dEutqvwqYr5ho6678ahSe
     * identityAddress : buQtvtDePDTYYdv4F3A4zNC4vE4Nu4nMkgum
     * deviceId : andorid
     * signData : 5EF172BFE3FD55A5B0606F131DB81C355805FBCD1F4A7FA8FF618D43D309E07872D08691EDACFC8AED08DD5F946AA610BA6F28D95C70DAB25EA08C9AEE79FE0A
     * publicKey : b001ec0225bb37cec7a4cb4ba47c61eab8e23290750a06189c3a98f12f2385f5b8f854d73dbc
     */

    private String walletAddress;
    private String identityAddress;
    private String deviceId;
    private String signData;
    private String publicKey;

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getIdentityAddress() {
        return identityAddress;
    }

    public void setIdentityAddress(String identityAddress) {
        this.identityAddress = identityAddress;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSignData() {
        return signData;
    }

    public void setSignData(String signData) {
        this.signData = signData;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
