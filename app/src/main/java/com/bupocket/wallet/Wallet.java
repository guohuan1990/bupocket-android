package com.bupocket.wallet;

import com.alibaba.fastjson.JSON;
import com.bupocket.common.Constants;
import com.bupocket.wallet.model.WalletBPData;
import com.bupocket.wallet.utils.KeyStore;
import com.bupocket.wallet.utils.keystore.BaseKeyStoreEntity;
import io.bumo.SDK;
import io.bumo.common.ToBaseUnit;
import io.bumo.encryption.key.PrivateKey;
import io.bumo.encryption.utils.hex.HexFormat;
import io.bumo.model.request.*;
import io.bumo.model.request.operation.BUSendOperation;
import io.bumo.model.response.*;
import io.bumo.model.response.result.TransactionBuildBlobResult;

public class Wallet {
    SDK sdk = SDK.getInstance(Constants.BUMO_NODE_URL);
    private static Wallet wallet;

    private Wallet(){}

    public static Wallet getInstance(){
        if(wallet == null){
            wallet = new Wallet();
        }
        return wallet;
    }
    public byte[] getSkey(String password, String ciphertextSkeyData) throws Exception {
        BaseKeyStoreEntity baseKeyStoreEntity = JSON.parseObject(ciphertextSkeyData, BaseKeyStoreEntity.class);
        String skeyHex = KeyStore.decodeMsg(password,baseKeyStoreEntity);
        return HexFormat.hexToByte(skeyHex);
    }

    public String getAccountBUBalance(String accountAddress){
        AccountGetBalanceRequest request = new AccountGetBalanceRequest();
        request.setAddress(accountAddress);
        AccountGetBalanceResponse response = sdk.getAccountService().getBalance(request);

        System.out.println(JSON.toJSONString(response, true));
        if (0 == response.getErrorCode()) {
            return ToBaseUnit.MO2BU(response.getResult().getBalance().toString());
        }
        return null;
    }


    public String sendBu(String password,String bPData, String fromAccAddr,String toAccAddr,String amount, String note, String fee) throws Exception {

        String senderPrivateKey = null;
                WalletBPData walletBPData = JSON.parseObject(bPData, WalletBPData.class);

        if(walletBPData.getAccounts().size()>0){
            for (WalletBPData.AccountsBean accountsBean:walletBPData.getAccounts()
                 ) {
                if(fromAccAddr.equals(accountsBean.getAddress())){
                    senderPrivateKey = KeyStore.decodeMsg(password,JSON.parseObject(accountsBean.getSecret().toString(),BaseKeyStoreEntity.class));
                    break;
                }
            }
        }
        // Init variable
        // The account address to receive bu
        String destAddress = toAccAddr;
        // The amount to be sent
        Long sendAmount = ToBaseUnit.BU2MO(amount);
        // The fixed write 1000L, the unit is MO
        Long gasPrice = 1000L;
        // Set up the maximum cost 0.01BU
        Long feeLimit = ToBaseUnit.BU2MO(fee);
        // Transaction initiation account's nonce + 1

        Long nonce = getAccountNonce(fromAccAddr) + 1;

        // Record txhash for subsequent confirmation of the real result of the transaction.
        // After recommending five blocks, call again through txhash `Get the transaction information
        // from the transaction Hash'(see example: getTxByHash ()) to confirm the final result of the transaction
        return sendBu(senderPrivateKey, destAddress, sendAmount, nonce, gasPrice, feeLimit);
    }
    private String sendBu(String senderPrivateKey, String destAddress, Long amount, Long senderNonce, Long gasPrice, Long feeLimit) throws Exception {

        // 1. Get the account address to send this transaction
        String senderAddresss = getAddressByPrivateKey(senderPrivateKey);

        // 2. Build sendBU
        BUSendOperation buSendOperation = new BUSendOperation();
        buSendOperation.setSourceAddress(senderAddresss);
        buSendOperation.setDestAddress(destAddress);
        buSendOperation.setAmount(amount);

        // 3. Build transaction
        TransactionBuildBlobRequest transactionBuildBlobRequest = new TransactionBuildBlobRequest();
        transactionBuildBlobRequest.setSourceAddress(senderAddresss);
        transactionBuildBlobRequest.setNonce(senderNonce);
        transactionBuildBlobRequest.setFeeLimit(feeLimit);
        transactionBuildBlobRequest.setGasPrice(gasPrice);
        transactionBuildBlobRequest.addOperation(buSendOperation);

        // 4. Build transaction BLob
        String transactionBlob = null;
        TransactionBuildBlobResponse transactionBuildBlobResponse = sdk.getTransactionService().buildBlob(transactionBuildBlobRequest);
        TransactionBuildBlobResult transactionBuildBlobResult = transactionBuildBlobResponse.getResult();
        String txHash = transactionBuildBlobResult.getHash();
        transactionBlob = transactionBuildBlobResult.getTransactionBlob();

        // 5. Sign transaction BLob
        String[] signerPrivateKeyArr = {senderPrivateKey};
        TransactionSignRequest transactionSignRequest = new TransactionSignRequest();
        transactionSignRequest.setBlob(transactionBlob);
        for (int i = 0; i < signerPrivateKeyArr.length; i++) {
            transactionSignRequest.addPrivateKey(signerPrivateKeyArr[i]);
        }
        TransactionSignResponse transactionSignResponse = sdk.getTransactionService().sign(transactionSignRequest);

        // 6. Broadcast transaction
        TransactionSubmitRequest transactionSubmitRequest = new TransactionSubmitRequest();
        transactionSubmitRequest.setTransactionBlob(transactionBlob);
        transactionSubmitRequest.setSignatures(transactionSignResponse.getResult().getSignatures());
        TransactionSubmitResponse transactionSubmitResponse = sdk.getTransactionService().submit(transactionSubmitRequest);
        if (0 == transactionSubmitResponse.getErrorCode()) {
            System.out.println("Success，hash=" + transactionSubmitResponse.getResult().getHash());
        } else {
            System.out.println("Failure，hash=" + transactionSubmitResponse.getResult().getHash() + "");
            System.out.println(JSON.toJSONString(transactionSubmitResponse, true));
        }
        return txHash;
    }

    private String getAddressByPrivateKey(String privatekey) throws Exception {
        String publicKey = PrivateKey.getEncPublicKey(privatekey);
        String address = PrivateKey.getEncAddress(publicKey);
        return address;
    }
    private Long getAccountNonce(String accountAddress){
        AccountGetNonceRequest request = new AccountGetNonceRequest();
        request.setAddress(accountAddress);

        AccountGetNonceResponse response = sdk.getAccountService().getNonce(request);
        if (0 == response.getErrorCode()) {
           return response.getResult().getNonce();
        }
        return null;
    }
}
