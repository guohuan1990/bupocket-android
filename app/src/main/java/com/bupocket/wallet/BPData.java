package com.bupocket.wallet;

import java.util.List;

public class BPData {
    private String skey;
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

    public static class AccountsBean {
        /**
         * address :
         * secret :
         */

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
