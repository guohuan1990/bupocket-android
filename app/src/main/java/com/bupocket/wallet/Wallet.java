package com.bupocket.wallet;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.bupocket.common.Constants;
import com.bupocket.http.api.AccountService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.DecimalCalculate;
import com.bupocket.wallet.enums.BUChainExceptionEnum;
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
import io.bumo.model.request.operation.AccountActivateOperation;
import io.bumo.model.request.operation.AssetSendOperation;
import io.bumo.model.request.operation.BUSendOperation;
import io.bumo.model.request.operation.BaseOperation;
import io.bumo.model.response.*;
import io.bumo.model.response.result.TransactionBuildBlobResult;
import org.bitcoinj.crypto.MnemonicCode;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private WalletBPData create(String password, String sKey,Context context) throws WalletException {
        try {
            WalletBPData walletBPData;
            List<String> mnemonicCodes;
            BaseKeyStoreEntity baseKeyStoreEntity =  KeyStore.encryptMsg(password, sKey, com.bupocket.wallet.Constants.WALLET_STORE_N, com.bupocket.wallet.Constants.WALLET_STORE_R, com.bupocket.wallet.Constants.WALLET_STORE_P, 1);
            List<String> hdPaths = new ArrayList<>();
            hdPaths.add("M/44H/526H/0H/0/0");
            hdPaths.add("M/44H/526H/1H/0/0");
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
            WalletBPData.AccountsBean identityAccountBean = accountsBeans.get(0);
            WalletBPData.AccountsBean walletAccountBean = accountsBeans.get(1);

            String walletAccountPk = getPk(privateKeys.get(1));

            String walletAccountSignData = signData(privateKeys.get(1), walletAccountBean.getAddress());

            deviceBind(walletAccountBean.getAddress(),identityAccountBean.getAddress(),walletAccountPk,walletAccountSignData,context);

            return walletBPData;

        } catch (Exception e) {
            e.printStackTrace();
            throw new WalletException(ExceptionEnum.SYS_ERR.getCode(), ExceptionEnum.SYS_ERR.getMessage());
        }
    }
    public WalletBPData create(String password, Context context) throws WalletException {
        byte[] aesIv = new byte[16];
        SecureRandom randomIv = new SecureRandom();
        randomIv.nextBytes(aesIv);
        String skey = HexFormat.byteToHex(aesIv);
        return create(password, skey,context);
    }

    public WalletBPData updateAccountPassword(String oblPwd,String newPwd, String ciphertextSkeyData, Context context) throws WalletException {
        try {
            // 校验密码是否匹配
            String sKey =  HexFormat.byteToHex(getSkey(oblPwd,ciphertextSkeyData));
            return create(newPwd,sKey,context);
        }catch (Exception e){
            e.printStackTrace();
            throw new WalletException(ExceptionEnum.SYS_ERR.getCode(),ExceptionEnum.SYS_ERR.getMessage());
        }
    }


    public WalletBPData importMnemonicCode(List<String> mnemonicCodes,String password, Context context) throws Exception {
        byte[] sKeyByte = new MnemonicCode().toEntropy(mnemonicCodes);
        String sKey = HexFormat.byteToHex(sKeyByte);
        return create(password, sKey,context);
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
            String senderPrivateKey = getPKBYAccountPassword(password, bPData, fromAccAddr);
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


    public String sendToken(String password,String bPData, String fromAccAddr,String toAccAddr,String tokenCode,String tokenIssuer,String sendTokenAamount,String tokenDecimals, String note, String fee) throws Exception {
        String hash = null;
        try {
            String senderPrivateKey = getPKBYAccountPassword(password, bPData, fromAccAddr);
            hash = sendToken(senderPrivateKey,tokenIssuer,toAccAddr,tokenCode,sendTokenAamount,tokenDecimals,note,fee);
        }catch (WalletException e){
            throw new WalletException(e.getErrCode(),e.getErrMsg());
        }catch (Exception e){
            throw new Exception(e);
        }
        return hash;
    }

    private String getPKBYAccountPassword(String password, String bPData, String fromAccAddr) throws Exception {
        String senderPrivateKey = null;
        List<WalletBPData.AccountsBean> accountsBeans = JSON.parseArray(bPData, WalletBPData.AccountsBean.class);

        if (accountsBeans.size() > 0) {
            for (WalletBPData.AccountsBean accountsBean : accountsBeans
            ) {
                if (fromAccAddr.equals(accountsBean.getAddress())) {
                    senderPrivateKey = KeyStore.decodeMsg(password, JSON.parseObject(accountsBean.getSecret().toString(), BaseKeyStoreEntity.class));
                    if (!senderPrivateKey.startsWith("priv")) {
                        throw new Exception();
                    }
                    break;
                }
            }
        }
        return senderPrivateKey;
    }


    private String sendToken(String senderPrivateKey, String issuerAddress,String destAddress ,String code ,String amount,String decimals, String note,String fee) throws Exception {
        // The operation notes
        String metadata = note;
        // The fixed write 1000L, the unit is MO
        Long gasPrice = 1000L;


        // handle send token amount

        Long sendTokenAmount = handleSendTokenAmount(amount, decimals);

        // Transaction initiation account's Nonce + 1
        String senderAddress = getAddressByPrivateKey(senderPrivateKey);
        Long nonce = getAccountNonce(senderAddress) + 1;


        List<BaseOperation> operations = new ArrayList<>();


        String senderAddresss = getAddressByPrivateKey(senderPrivateKey);

        // Check whether the destination account is activated
        if (!checkAccountActivated(destAddress)) {
            if(Double.parseDouble(fee) - Constants.ACTIVE_AMOUNT_FEE <= 0){
                throw new WalletException(ExceptionEnum.FEE_NOT_ENOUGH);
            }
            AccountActivateOperation accountActivateOperation = new AccountActivateOperation();
            accountActivateOperation.setSourceAddress(senderAddresss);
            accountActivateOperation.setDestAddress(destAddress);
            accountActivateOperation.setInitBalance(ToBaseUnit.BU2MO(Constants.ACTIVE_AMOUNT_FEE+""));
            accountActivateOperation.setMetadata("activate account");
            operations.add(accountActivateOperation);
            fee = DecimalCalculate.sub(Double.parseDouble(fee), Constants.ACTIVE_AMOUNT_FEE) + "";
        }

        // Set up the maximum cost 0.01BU
        Long feeLimit = ToBaseUnit.BU2MO(fee);

        // Build asset operation
        AssetSendOperation assetSendOperation = new AssetSendOperation();
        assetSendOperation.setSourceAddress(senderAddresss);
        assetSendOperation.setDestAddress(destAddress);
        assetSendOperation.setCode(code);
        assetSendOperation.setAmount(sendTokenAmount);
        assetSendOperation.setIssuer(issuerAddress);
        assetSendOperation.setMetadata(metadata);

        operations.add(assetSendOperation);

//        BaseOperation[] operations = {operation};
        // Record txhash for subsequent confirmation of the real result of the transaction.
        // After recommending five blocks, call again through txhash `Get the transaction information
        // from the transaction Hash'(see example: getTxByHash ()) to confirm the final result of the transaction
        String txHash = submitTransaction(senderPrivateKey, senderAddresss, operations, nonce, gasPrice, feeLimit, note);
        return txHash;
    }


    private Long handleSendTokenAmount(String srcAmount, String decimals){

        return Long.parseLong(BigDecimal.valueOf(DecimalCalculate.mul(Double.parseDouble(srcAmount),Math.pow(10, Double.parseDouble(decimals)))).setScale(0).toPlainString());
    }

    private String submitTransaction(String senderPrivateKey, String senderAddresss, List<BaseOperation> operations, Long senderNonce, Long gasPrice, Long feeLimit, String transMetadata)throws Exception {
        // 1. Build transaction
        TransactionBuildBlobRequest transactionBuildBlobRequest = new TransactionBuildBlobRequest();
        transactionBuildBlobRequest.setSourceAddress(senderAddresss);
        transactionBuildBlobRequest.setNonce(senderNonce);
        transactionBuildBlobRequest.setFeeLimit(feeLimit);
        transactionBuildBlobRequest.setGasPrice(gasPrice);
        transactionBuildBlobRequest.setMetadata(transMetadata);
        for (int i = 0; i < operations.size(); i++) {
            transactionBuildBlobRequest.addOperation(operations.get(i));
        }

        transactionBuildBlobRequest.setMetadata(transMetadata);

        // 2. Build transaction BLob
        String transactionBlob;
        TransactionBuildBlobResponse transactionBuildBlobResponse = sdk.getTransactionService().buildBlob(transactionBuildBlobRequest);
        if (transactionBuildBlobResponse.getErrorCode() != 0) {
            System.out.println("error: " + transactionBuildBlobResponse.getErrorDesc());
            return null;
        }
        TransactionBuildBlobResult transactionBuildBlobResult = transactionBuildBlobResponse.getResult();
        transactionBlob = transactionBuildBlobResult.getTransactionBlob();

        // 3. Sign transaction BLob
        String[] signerPrivateKeyArr = {senderPrivateKey};
        TransactionSignRequest transactionSignRequest = new TransactionSignRequest();
        transactionSignRequest.setBlob(transactionBlob);
        for (int i = 0; i < signerPrivateKeyArr.length; i++) {
            transactionSignRequest.addPrivateKey(signerPrivateKeyArr[i]);
        }
        TransactionSignResponse transactionSignResponse = sdk.getTransactionService().sign(transactionSignRequest);
        if (transactionSignResponse.getErrorCode() != 0) {
            System.out.println("error: " + transactionSignResponse.getErrorDesc());
            return null;
        }

        // 4. Broadcast transaction
        String Hash = null;
        TransactionSubmitRequest transactionSubmitRequest = new TransactionSubmitRequest();
        transactionSubmitRequest.setTransactionBlob(transactionBlob);
        transactionSubmitRequest.setSignatures(transactionSignResponse.getResult().getSignatures());
        TransactionSubmitResponse transactionSubmitResponse = sdk.getTransactionService().submit(transactionSubmitRequest);
        if (0 == transactionSubmitResponse.getErrorCode()) {
            Hash = transactionSubmitResponse.getResult().getHash();
        } else {
            if(BUChainExceptionEnum.ERRCODE_FEE_NOT_ENOUGH.getCode().equals(transactionSubmitResponse.getErrorCode())){
                throw new WalletException(ExceptionEnum.FEE_NOT_ENOUGH);
            }else if(BUChainExceptionEnum.ERRCODE_ACCOUNT_LOW_RESERVE.getCode().equals(transactionSubmitResponse.getErrorCode())){
                throw new WalletException(ExceptionEnum.BU_NOT_ENOUGH);
            }
            System.out.println(JSON.toJSONString(transactionSubmitResponse, true));
        }
        return Hash;
    }

    /**
     * Check whether an account is activated.\
     */
    public boolean checkAccountActivated(String address) {
        AccountCheckActivatedRequst request = new AccountCheckActivatedRequst();
        request.setAddress(address);

        AccountCheckActivatedResponse response = sdk.getAccountService().checkActivated(request);
        if (response.getErrorCode() != 0) {
            return false;
        }
        System.out.println(response.getResult().getIsActivated());
        return response.getResult().getIsActivated();
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

    private void deviceBind(final String walletAddress, final String identityAddress, final String publicKey, final String signData, final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {

                AccountService accountService = RetrofitFactory.getInstance().getRetrofit().create(AccountService.class);
                Map<String, Object> parmasMap = new HashMap<>();
                parmasMap.put("walletAddress",walletAddress);
                parmasMap.put("identityAddress", identityAddress);
                parmasMap.put("deviceId", CommonUtil.getUniqueId(context));
                parmasMap.put("publicKey",publicKey);
                parmasMap.put("signData",signData);

                Call<ApiResult> call = accountService.deviceBind(parmasMap);
                call.enqueue(new Callback<ApiResult>() {
                    @Override
                    public void onResponse(Call<ApiResult> call, Response<ApiResult> response) {
                        ApiResult respDto = response.body();
                    }

                    @Override
                    public void onFailure(Call<ApiResult> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        }).start();
    }


    private String getPk(String sk){
        return PrivateKey.getEncPublicKey(sk);
    }


    private String signData(String sk,String message){
        return HexFormat.byteToHex(PrivateKey.sign(message.getBytes(), sk));
    }



}
