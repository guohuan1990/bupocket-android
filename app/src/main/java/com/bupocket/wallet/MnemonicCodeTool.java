package com.bupocket.wallet;

import io.bumo.encryption.key.PrivateKey;
import io.bumo.encryption.utils.hex.HexFormat;
import org.bitcoinj.crypto.*;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


public class MnemonicCodeTool {
    private InputStream wordstream;

    public MnemonicCodeTool(InputStream wordstream) {
        this.wordstream = wordstream;
    }

    public List<String> toMnemonic(byte[] entropy) throws IOException, MnemonicException.MnemonicLengthException {
        MnemonicCode mnemonicCode  = new MnemonicCode(wordstream, null);
        List<String> wds = mnemonicCode.toMnemonic(entropy);
        return wds;
    }

    public List<Account> toPKs(List<String> mnemonicCodeList, List<String> hdPaths) throws Exception {
        if(hdPaths == null || hdPaths.size() <= 0){
            return null;
        }
        byte[] seed = MnemonicCode.toSeed(mnemonicCodeList, "");
        DeterministicKey deterministicKey = HDKeyDerivation.createMasterPrivateKey(seed);
        DeterministicHierarchy deterministicHierarchy = new DeterministicHierarchy(deterministicKey);
        List<Account> accounts = new ArrayList<>();
        List<ChildNumber> keyPath = null;
        Account account = null;
        for (String hdPath: hdPaths) {
            keyPath = HDUtils.parsePath(hdPath);
            DeterministicKey childKey = deterministicHierarchy.get(keyPath, true, true);
            BigInteger privKeyHex = childKey.getPrivKey();
            PrivateKey privateKey = new PrivateKey(HexFormat.hexStringToBytes(privKeyHex.toString(16)));
            account = new Account();
            account.setAddress(privateKey.getEncAddress());
            account.setPrivateKey(privateKey.getEncPrivateKey());
            account.setPublicKey(privateKey.getEncPublicKey());
            accounts.add(account);
        }

        return accounts;
    }
}
