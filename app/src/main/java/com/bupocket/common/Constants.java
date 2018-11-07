package com.bupocket.common;

public class Constants {
    //开发环境
//    public static final String WEB_SERVER_DOMAIN = "http://52.80.218.114:8081/";
//    public static final String BUMO_NODE_URL = "http://seed1.bumotest.io:26002";
//    public static final String PUSH_MESSAGE_SOCKET_URL = "http://192.168.6.98:3100/";
    //测试环境
    public static final String WEB_SERVER_DOMAIN = "http://api-bp.bumotest.io/";
    public static final String BUMO_NODE_URL = "http://wallet-node.bumotest.io";
    public static final String PUSH_MESSAGE_SOCKET_URL = "https://ws-tools.bumotest.io";
    //生产环境
//    public static final String WEB_SERVER_DOMAIN = "https://api-bp.bumo.io/";
//    public static final String BUMO_NODE_URL = "http://wallet-node.bumo.io";
//    public static final String PUSH_MESSAGE_SOCKET_URL = "https://ws-tools.bumo.io";
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

    public static final Integer TX_REQUEST_TIMEOUT_TIMES = 20;

}
