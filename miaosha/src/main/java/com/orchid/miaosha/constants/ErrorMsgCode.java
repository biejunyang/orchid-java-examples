package com.orchid.miaosha.constants;

public class ErrorMsgCode {
    private int code;
    private String message;


    //登录模块异常状态码 500200
    public static ErrorMsgCode LOGIN_ERROR=new ErrorMsgCode(500101, "登录失败");
    public static ErrorMsgCode LOGIN_USERNAME_ERROR=new ErrorMsgCode(500102, "用户名不存在");
    public static ErrorMsgCode LOGIN_PASSWORD_ERROR=new ErrorMsgCode(500103, "密码错误");





    ErrorMsgCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
