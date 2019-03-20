package com.assetMarket.wallet.exception;

import com.assetMarket.wallet.enums.ExceptionEnum;

public class WalletException extends Exception{
    private static final long serialVersionUID = 429654902433634386L;
    private String errCode;
    private String errMsg;

    public WalletException(String message, Throwable cause) {
        super(message, cause);
        this.errCode = ExceptionEnum.SYS_ERR.getCode();
        this.errMsg = message;
    }

    public WalletException(Throwable cause) {
        super(cause);
        this.errCode = ExceptionEnum.SYS_ERR.getCode();
        this.errMsg = ExceptionEnum.SYS_ERR.getMessage();
    }

    public WalletException(String errCode, String message) {
        this.errCode = errCode;
        this.errMsg = message;
    }

    public WalletException(ExceptionEnum errEnum) {
        this(errEnum.getCode(), errEnum.getMessage());
    }

    public String getErrCode() {
        return errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }
}
