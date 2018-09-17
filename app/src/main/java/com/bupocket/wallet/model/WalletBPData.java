package com.bupocket.wallet.model;

import java.util.List;

public class WalletBPData {
    private String skey;
    private List<String> mnemonicCodes;
    private List<AccountsBean> accounts;

    public List<AccountsBean> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountsBean> accounts) {
        this.accounts = accounts;
    }

    public String getSkey() {
        return skey;
    }

    public void setSkey(String skey) {
        this.skey = skey;
    }

    public List<String> getMnemonicCodes() {
        return mnemonicCodes;
    }

    public void setMnemonicCodes(List<String> mnemonicCodes) {
        this.mnemonicCodes = mnemonicCodes;
    }

    public static class AccountsBean {

        private String address;
        private String secret;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }
}
