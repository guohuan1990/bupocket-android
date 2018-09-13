package com.bupocket.wallet.utils;

import com.bupocket.wallet.utils.keystore.BaseKeyStoreEntity;
import com.bupocket.wallet.utils.keystore.KeyStoreEntity;
import io.bumo.encryption.crypto.keystore.entity.ScryptParamsEty;
import io.bumo.encryption.key.PrivateKey;
import io.bumo.encryption.model.KeyType;
import io.bumo.encryption.utils.aes.AesCtr;
import io.bumo.encryption.utils.hex.HexFormat;
import io.bumo.encryption.utils.scrypt.SCrypt;

import java.security.SecureRandom;

public class KeyStore {

    public static BaseKeyStoreEntity encryptMsg(String password, String msg, int n, int r, int p, int version) throws Exception {
        BaseKeyStoreEntity keyStoreEntity = new BaseKeyStoreEntity();
        int dkLen = 32;
        byte[] salt = new byte[32];
        SecureRandom randomSalt = new SecureRandom();
        randomSalt.nextBytes(salt);
        byte[] aesIv = new byte[16];
        SecureRandom randomIv = new SecureRandom();
        randomIv.nextBytes(aesIv);
        byte[] dk = SCrypt.scrypt(password.getBytes(), salt, n, r, p, dkLen);
        byte[] cyperText = AesCtr.encrypt(msg.getBytes(), dk, aesIv);
        keyStoreEntity.setVersion(version);
        keyStoreEntity.setAesctr_iv(HexFormat.byteToHex(aesIv));
        keyStoreEntity.setCypher_text(HexFormat.byteToHex(cyperText));
        ScryptParamsEty scryptParams = new ScryptParamsEty();
        scryptParams.setN(n);
        scryptParams.setP(p);
        scryptParams.setR(r);
        scryptParams.setSalt(HexFormat.byteToHex(salt));
        keyStoreEntity.setScrypt_params(scryptParams);
        return keyStoreEntity;
    }

    public static KeyStoreEntity generateKeyStore(String password, String privateKeyStr, int n, int r, int p, int version) throws Exception {
        KeyStoreEntity keyStoreEty = new KeyStoreEntity();
        int dkLen = 32;
        byte[] salt = new byte[32];
        SecureRandom randomSalt = new SecureRandom();
        randomSalt.nextBytes(salt);
        byte[] aesIv = new byte[16];
        SecureRandom randomIv = new SecureRandom();
        randomIv.nextBytes(aesIv);
        byte[] dk = SCrypt.scrypt(password.getBytes(), salt, n, r, p, dkLen);
        String address = "";
        PrivateKey privateKey;
        if (privateKeyStr != null && !privateKeyStr.isEmpty() && !"".equals(privateKeyStr)) {
            privateKey = new PrivateKey(privateKeyStr);
            address = privateKey.getEncAddress();
        } else {
            privateKey = new PrivateKey(KeyType.ED25519);
            privateKeyStr = privateKey.getEncPrivateKey();
            address = privateKey.getEncAddress();
        }

        byte[] cyperText = AesCtr.encrypt(privateKeyStr.getBytes(), dk, aesIv);
        keyStoreEty.setAddress(address);
        keyStoreEty.setVersion(version);
        keyStoreEty.setAesctr_iv(HexFormat.byteToHex(aesIv));
        keyStoreEty.setCypher_text(HexFormat.byteToHex(cyperText));
        ScryptParamsEty scryptParams = new ScryptParamsEty();
        scryptParams.setN(n);
        scryptParams.setP(p);
        scryptParams.setR(r);
        scryptParams.setSalt(HexFormat.byteToHex(salt));
        keyStoreEty.setScrypt_params(scryptParams);
        return keyStoreEty;
    }

    public static String decipherKeyStore(String password, KeyStoreEntity keyStoreEntity) throws Exception {
        ScryptParamsEty scryptParams = keyStoreEntity.getScrypt_params();
        int n = scryptParams.getN();
        int r = scryptParams.getR();
        int p = scryptParams.getP();
        byte[] salt = HexFormat.hexToByte(scryptParams.getSalt());
        int keyLen = 32;
        byte[] aesIv = HexFormat.hexToByte(keyStoreEntity.getAesctr_iv());
        byte[] cypherText = HexFormat.hexToByte(keyStoreEntity.getCypher_text());
        String address = keyStoreEntity.getAddress();
        byte[] dk = SCrypt.scrypt(password.getBytes(), salt, n, r, p, keyLen);
        String encPrivateKey = AesCtr.decrypt(cypherText, dk, aesIv);
        PrivateKey privateKey = new PrivateKey(encPrivateKey);
        if (!privateKey.getEncAddress().equals(address)) {
            throw new Exception("the address in the keyStore was wrong, please check");
        } else {
            return encPrivateKey;
        }
    }

    public static String decodeMsg(String password, BaseKeyStoreEntity baseKeyStoreEntity) throws Exception {
        ScryptParamsEty scryptParams = baseKeyStoreEntity.getScrypt_params();
        int n = scryptParams.getN();
        int r = scryptParams.getR();
        int p = scryptParams.getP();
        byte[] salt = HexFormat.hexToByte(scryptParams.getSalt());
        int keyLen = 32;
        byte[] aesIv = HexFormat.hexToByte(baseKeyStoreEntity.getAesctr_iv());
        byte[] cypherText = HexFormat.hexToByte(baseKeyStoreEntity.getCypher_text());

        byte[] dk = SCrypt.scrypt(password.getBytes(), salt, n, r, p, keyLen);
        String msg = AesCtr.decrypt(cypherText, dk, aesIv);
        return msg;
    }
}
