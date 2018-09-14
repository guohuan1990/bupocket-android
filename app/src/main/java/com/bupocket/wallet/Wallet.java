package com.bupocket.wallet;

import com.alibaba.fastjson.JSON;
import com.bupocket.common.Constants;
import com.bupocket.wallet.utils.KeyStore;
import com.bupocket.wallet.utils.keystore.BaseKeyStoreEntity;
import io.bumo.SDK;
import io.bumo.common.ToBaseUnit;
import io.bumo.encryption.utils.hex.HexFormat;
import io.bumo.model.request.AccountGetBalanceRequest;
import io.bumo.model.response.AccountGetBalanceResponse;

public class Wallet {
    public static byte[] getSkey(String password, String ciphertextSkeyData) throws Exception {
        BaseKeyStoreEntity baseKeyStoreEntity = JSON.parseObject(ciphertextSkeyData, BaseKeyStoreEntity.class);
        String skeyHex = KeyStore.decodeMsg(password,baseKeyStoreEntity);
        return HexFormat.hexToByte(skeyHex);
    }

    public static String getAccountBUBalance(String accountAddress){
        AccountGetBalanceRequest request = new AccountGetBalanceRequest();
        request.setAddress(accountAddress);
        SDK sdk = SDK.getInstance(Constants.BUMO_NODE_URL);
        AccountGetBalanceResponse response = sdk.getAccountService().getBalance(request);

        System.out.println(JSON.toJSONString(response, true));
        if (0 == response.getErrorCode()) {
            return ToBaseUnit.MO2BU(response.getResult().getBalance().toString());
        }
        return null;
    }


}
