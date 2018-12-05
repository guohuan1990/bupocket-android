package com.bupocket.enums;

public enum ExceptionEnum {
    SUCCESS("0", "success"),
    FAIL("1","系统内部错误"),
    SYS_ERR("400000", "系统内部错误"),
    ARG_ERR("400001", "参数校验失败"),

    USER_TOKEN_ERR("100001", "无效的用户令牌"),
    PUBLICKEY_ERROR("100002", "非法公钥"),
    SIGN_VERIFY_ERROR("100003", "签名校验失败"),
    MERCHANT_NOT_EXIST("100004", "商户不存在"),
    BU_BALANCE_ERROR("100005", "账户BU余额不足"),
    ASSET_BALANCE_ERROR("100006", "账户资产余额不足"),

    ADVERT_ID_EXIST_ERROR("200001", "广告不存在"),
    ADVERT_SELL_PUBLISH_BLOCK_CHAIN_TX_FAIL("200002","该广告上链失败"),
    ADVERT_STOCK_QUANTITY_ERROR("200003","广告存库不足"),
    ADVERT_PRICE_ERROR1("200004","广告价格发生变化"),

    ORDER_BCTX_FAIL("300001","区块链转账失败"),
    ORDER_PLACE_ONESELF_ERROR("300002","不能下单自己的广告"),

    ASSET_INFO_NOT_EXIST("500001","资产信息不存在"),
    QUERY_ASSET_TO_BLOCKCHAIN_ERROR("500002","资产信息查询失败"),
    SELL_ASSET_LESS_ERROR("500003","出售资产不足"),

    COMM_GET_ACCOUNT_BALANCE_ERROR("600001","获取账户余额异常，请稍候重试"),
    COMM_ACCOUNT_BALANCE_LESS_ERROR("600002","账户余额不足"),
    COMM_CREATE_BLOB_DATA_ERROR("600003", "交易数据打包失败，请稍候重试"),
    COMM_BLOB_NOT_EXIST_ERROR("600004","blob信息不存在"),
    COMM_OPERATION_NOT_EXIST_ERROR("600005","操作信息不存在"),
    ;

    private final String code;
    private final String message;

    private ExceptionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }


    public String getMessage() {
        return message;
    }
}
