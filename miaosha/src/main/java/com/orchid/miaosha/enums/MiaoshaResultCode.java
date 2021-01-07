package com.orchid.miaosha.enums;


import com.orchid.core.ResultCode;

/**
 * 返回状态码定义
 */
public enum MiaoshaResultCode implements ResultCode {

    /*成功状态码*/
    SUCCESS(200, "success"),

    /*错误状态码*/
    SERVER_ERROR(500, "服务器异常"),

    /*登录错误码*/
    LOGIN_ERROR(1000, "登录错误"),
    LOGIN_USERNAME_ERROR(1001, "登录错误"),
    LOGIN_PASSWORD_ERROR(1002, "登录错误");


    private int code;
    private String message;

    MiaoshaResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String msg() {
        return message;
    }
}
