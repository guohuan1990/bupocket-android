package com.assetMarket.common;

public class Constants {

    public static String WEB_SERVER_DOMAIN = MainNetConfig.WEB_SERVER_DOMAIN.getValue();
    public static String BUMO_NODE_URL = MainNetConfig.BUMO_NODE_URL.getValue();
    public static String PUSH_MESSAGE_SOCKET_URL = MainNetConfig.PUSH_MESSAGE_SOCKET_URL.getValue();

    public static final Integer SEND_TOKEN_NOTE_MAX_LENGTH = 20;
    public static final Integer HELP_FEEDBACK_CONTENT_LENGTH = 100;
    public static final Integer HELP_FEEDBACK_CONTACT_LENGTH = 20;
    public static final double MIN_FEE = 0.005;
    public static final double MAX_FEE = 10;
    public static final double RESERVE_AMOUNT = 0.02;
    public static final Double ACTIVE_AMOUNT_FEE = 0.02;
    public static final double MIN_SEND_AMOUNT = 0.00000001;
    public static final double MAX_SEND_AMOUNT = 1000000000;
    public static final String REGISTER_TOKEN_FEE = "0.02";
    public static final String ISSUE_TOKEN_FEE = "50.01";
    public static final String ACCOUNT_NOT_ACTIVATED_SEND_FEE = "0.03";
    public static final String ACCOUNT_ACTIVATED_SEND_FEE = "0.01";
    public static final Integer DEFAULT_BUMO_NODE = 1;
    public static final Integer APP_TYPE_CODE = 1;

    public static final Integer TX_REQUEST_TIMEOUT_TIMES = 20;
    public static final Integer BU_DECIMAL = 8;
    public static final double CARD_TX_FEE = 0.5;

    public  static final String LOCAL_SHARED_FILE_NAME = "buPocket";

    public static enum MainNetConfig {
        WEB_SERVER_DOMAIN("https://api-bp.bumo.io/"),
        BUMO_NODE_URL("http://wallet-node.bumo.io"),
        PUSH_MESSAGE_SOCKET_URL("https://ws-tools.bumo.io");
        private String value;
        MainNetConfig(String value){
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    public static enum TestNetConfig {
//        WEB_SERVER_DOMAIN("http://52.80.218.114:8081/"),
        WEB_SERVER_DOMAIN("http://walletapp.bumeng.cn/"),
        BUMO_NODE_URL("http://wallet-node.bumotest.io"),
        PUSH_MESSAGE_SOCKET_URL("https://ws-tools.bumotest.io");
        private String value;
        TestNetConfig(String value){
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }
}
