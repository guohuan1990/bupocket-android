package com.bupocket.wallet;

import com.alibaba.fastjson.JSON;
import com.bupocket.common.Constants;
import com.bupocket.wallet.enums.ExceptionEnum;
import com.bupocket.wallet.exception.WalletException;
import com.bupocket.wallet.model.WalletBPData;
import com.bupocket.wallet.utils.KeyStore;
import com.bupocket.wallet.utils.keystore.BaseKeyStoreEntity;
import com.bupocket.wallet.utils.keystore.KeyStoreEntity;
import io.bumo.SDK;
import io.bumo.common.ToBaseUnit;
import io.bumo.encryption.crypto.mnemonic.Mnemonic;
import io.bumo.encryption.key.PrivateKey;
import io.bumo.encryption.utils.hex.HexFormat;
import io.bumo.model.request.*;
import io.bumo.model.request.operation.BUSendOperation;
import io.bumo.model.response.*;
import io.bumo.model.response.result.TransactionBuildBlobResult;
import org.bitcoinj.crypto.MnemonicCode;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

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

    private WalletBPData create(String password, String sKey) throws WalletException {
        try {
            WalletBPData walletBPData;
            List<String> mnemonicCodes;
            BaseKeyStoreEntity baseKeyStoreEntity =  KeyStore.encryptMsg(password, sKey, com.bupocket.wallet.Constants.WALLET_STORE_N, com.bupocket.wallet.Constants.WALLET_STORE_R, com.bupocket.wallet.Constants.WALLET_STORE_P, 1);
            List<String> hdPaths = new ArrayList<>();
            hdPaths.add("M/44/80/0/0/0");
            hdPaths.add("M/44/80/0/0/1");
            mnemonicCodes = new MnemonicCode().toMnemonic(HexFormat.hexStringToBytes(sKey));
            List<String> privateKeys = Mnemonic.generatePrivateKeys(mnemonicCodes,hdPaths);


            walletBPData = new WalletBPData();
            walletBPData.setSkey(JSON.toJSONString(baseKeyStoreEntity));
            List<WalletBPData.AccountsBean> accountsBeans = new ArrayList<>();

            KeyStoreEntity keyStoreEntity = null;
            WalletBPData.AccountsBean accountsBean;
            for (String pk:privateKeys) {
                keyStoreEntity = KeyStore.generateKeyStore(password, pk, com.bupocket.wallet.Constants.WALLET_STORE_N, com.bupocket.wallet.Constants.WALLET_STORE_R, com.bupocket.wallet.Constants.WALLET_STORE_P, 1);
                accountsBean = new WalletBPData.AccountsBean();
                accountsBean.setAddress(new PrivateKey(pk).getEncAddress());
                accountsBean.setSecret(JSON.toJSONString(keyStoreEntity));
                accountsBeans.add(accountsBean);
            }
            walletBPData.setAccounts(accountsBeans);
            walletBPData.setMnemonicCodes(mnemonicCodes);
            return walletBPData;

        } catch (Exception e) {
            e.printStackTrace();
            throw new WalletException(ExceptionEnum.SYS_ERR.getCode(), ExceptionEnum.SYS_ERR.getMessage());
        }
    }
    public WalletBPData create(String password) throws WalletException {
        byte[] aesIv = new byte[16];
        SecureRandom randomIv = new SecureRandom();
        randomIv.nextBytes(aesIv);
        String skey = HexFormat.byteToHex(aesIv);
        return create(password, skey);
    }

    public WalletBPData updateAccountPassword(String oblPwd,String newPwd, String ciphertextSkeyData) throws WalletException {
        try {
            // 校验密码是否匹配
            String sKey =  HexFormat.byteToHex(getSkey(oblPwd,ciphertextSkeyData));
            return create(newPwd,sKey);
        }catch (Exception e){
            e.printStackTrace();
            throw new WalletException(ExceptionEnum.SYS_ERR.getCode(),ExceptionEnum.SYS_ERR.getMessage());
        }
    }


    public WalletBPData importMnemonicCode(List<String> mnemonicCodes,String password) throws Exception {
        byte[] sKeyByte = new MnemonicCode().toEntropy(mnemonicCodes);
        String sKey = HexFormat.byteToHex(sKeyByte);
        return create(password, sKey);
    }

    public void checkPwd(String password,String ciphertextSkeyData) throws Exception {
        getSkey(password, ciphertextSkeyData);
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

    public Boolean checkAccAddress(String address){
        Boolean flag = false;
        AccountCheckValidRequest accountCheckValidRequest = new AccountCheckValidRequest();
        accountCheckValidRequest.setAddress(address);
        AccountCheckValidResponse accountCheckValidResponse = sdk.getAccountService().checkValid(accountCheckValidRequest);
        if (0 == accountCheckValidResponse.getErrorCode()) {
            return accountCheckValidResponse.getResult().isValid();
        } else {
            System.out.println(JSON.toJSONString(accountCheckValidResponse, true));
        }
        return flag;
    }


    public String sendBu(String password,String bPData, String fromAccAddr,String toAccAddr,String amount, String note, String fee) throws Exception {
        String hash = null;
        try {
            String senderPrivateKey = null;
            List<WalletBPData.AccountsBean> accountsBeans = JSON.parseArray(bPData, WalletBPData.AccountsBean.class);

            if(accountsBeans.size()>0){
                for (WalletBPData.AccountsBean accountsBean:accountsBeans
                        ) {
                    if(fromAccAddr.equals(accountsBean.getAddress())){
                        senderPrivateKey = KeyStore.decodeMsg(password,JSON.parseObject(accountsBean.getSecret().toString(),BaseKeyStoreEntity.class));
                        if(!senderPrivateKey.startsWith("priv")){
                            throw new Exception();
                        }
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
            String transMetadata = note;

            Long nonce = getAccountNonce(fromAccAddr) + 1;
            hash = sendBu(senderPrivateKey, destAddress, sendAmount, nonce, gasPrice, feeLimit,transMetadata);
        }catch (WalletException e){
            throw new WalletException(e.getErrCode(),e.getErrMsg());
        }catch (Exception e){
            throw new Exception(e);
        }
        return hash;
    }
    private String sendBu(String senderPrivateKey, String destAddress, Long amount, Long senderNonce, Long gasPrice, Long feeLimit,String transMetadata) throws Exception {

        String senderAddresss = getAddressByPrivateKey(senderPrivateKey);
        BUSendOperation buSendOperation = new BUSendOperation();
        buSendOperation.setSourceAddress(senderAddresss);
        buSendOperation.setDestAddress(destAddress);
        buSendOperation.setAmount(amount);

        TransactionBuildBlobRequest transactionBuildBlobRequest = new TransactionBuildBlobRequest();
        transactionBuildBlobRequest.setSourceAddress(senderAddresss);
        transactionBuildBlobRequest.setNonce(senderNonce);
        transactionBuildBlobRequest.setFeeLimit(feeLimit);
        transactionBuildBlobRequest.setGasPrice(gasPrice);
        transactionBuildBlobRequest.addOperation(buSendOperation);
        transactionBuildBlobRequest.setMetadata(transMetadata);

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
    private Long getAccountNonce(String accountAddress) throws WalletException{
        AccountGetNonceRequest request = new AccountGetNonceRequest();
        request.setAddress(accountAddress);

        AccountGetNonceResponse response = sdk.getAccountService().getNonce(request);
        if (0 == response.getErrorCode()) {
            return response.getResult().getNonce();
        }else if(11007 == response.getErrorCode()){
            throw new WalletException(response.getErrorCode().toString(), response.getErrorDesc());
        }
        return null;
    }



}
