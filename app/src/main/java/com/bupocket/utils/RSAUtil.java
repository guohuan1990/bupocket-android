package com.bupocket.utils;

import android.util.Base64;
import com.bupocket.common.Constants;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class RSAUtil {

    public static final String RSA = "RSA";
    public static final String TRANSFORMATION = "RSA/None/PKCS1Padding";
    private static RSAPublicKey publicKey = null;

    public static byte[] decryptByPrivateKey(byte[] encrypted, byte[] privateKey) throws Exception {
        // 得到私钥对象
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
        KeyFactory kf = KeyFactory.getInstance(RSA,"BC");
        PrivateKey keyPrivate = kf.generatePrivate(keySpec);
        // 解密数据
        Cipher cp = Cipher.getInstance(TRANSFORMATION);
        cp.init(Cipher.DECRYPT_MODE, keyPrivate);
        byte[] arr = cp.doFinal(encrypted);
        return arr;
    }

    public static String inputSteamToString(InputStream inputStream)
    {
        try
        {
            InputStreamReader inputReader = new InputStreamReader(inputStream);
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
            {
                if (line.charAt(0) == '-')
                {
                    continue;
                }
                Result += line;
            }
            return Result;
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static RSAPublicKey loadPublicKey(String pubKey) {
        try {
            byte[] buffer = Base64.decode(pubKey, Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA","BC");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decryptWithRSA(String encryedData,String pubKye) throws Exception {

        publicKey = loadPublicKey(pubKye);
        if (publicKey == null) {
            throw new NullPointerException("decrypt PublicKey is null !");
        }

        Cipher cipher = null;
        cipher = Cipher.getInstance(TRANSFORMATION);// 此处如果写成"RSA"解析的数据前多出来些乱码
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] output = cipher.doFinal(Base64.decode(encryedData, Base64.DEFAULT));
        return new String(output);
    }


}
