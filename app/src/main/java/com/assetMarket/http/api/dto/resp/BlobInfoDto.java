package com.assetMarket.http.api.dto.resp;

import com.google.gson.Gson;

public class BlobInfoDto {

    /**
     * txBlob : 0A2462755169724A6F56363695A5475347673544E755A52476D108088DEBE01
     * txHash : 0ce304668abb599c1082b98d5cbb980039578c52a0f9b2481a76fd9629b23e40
     * blobId : 123
     */

    private String txBlob;
    private String txHash;
    private String blobId;

    public static BlobInfoDto objectFromData(String str) {

        return new Gson().fromJson(str, BlobInfoDto.class);
    }

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
