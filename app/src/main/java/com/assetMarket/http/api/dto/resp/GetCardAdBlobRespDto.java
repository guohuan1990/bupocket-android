package com.assetMarket.http.api.dto.resp;

public class GetCardAdBlobRespDto {
    /**
     * txBlob : 0A246275516D79386F74627268546F6F7173694D533948516F36344A39624E54393239786A5A100318C0843D20E807320D313533363939393231353537313A55080712246275516D79386F74627268546F6F7173694D533948516F36344A39624E54393239786A5A522B0A24627551667A6E313872675A3242714A64326973756978724A337A476162734D434C59684A1080C2D72F
     * txHash : efa5b1a2d84a754b644afbe3ea73a8a0ac5a4d0714165bdf5caf22dd761ca7f9
     * blobId : 123
     */

    private String txBlob;
    private String txHash;
    private String blobId;

    public String getTxBlob() {
        return txBlob;
    }

    public void setTxBlob(String txBlob) {
        this.txBlob = txBlob;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getBlobId() {
        return blobId;
    }

    public void setBlobId(String blobId) {
        this.blobId = blobId;
    }
}
