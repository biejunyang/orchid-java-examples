package com.orchid.miaosha.Exception;


import com.orchid.core.Exception.BaseException;
import com.orchid.miaosha.constants.ErrorMsgCode;

public class MiaoShaException extends BaseException {

    public MiaoShaException(int code, String msg) {
        super(code, msg);
    }
    public MiaoShaException(String msg) {
        super(msg);
    }

    public MiaoShaException(ErrorMsgCode errorMsgCode) {
        this(errorMsgCode.getCode(),errorMsgCode.getMessage());
    }
}
