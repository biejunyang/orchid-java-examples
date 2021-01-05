package com.orchid.miaosha.common;

public class Result<T> {

    //状态：0失败,1成功
    private int status;

    //业务状态码
    private String code;

    //提示消息
    private String msg;

    //返回数据
    private T data;


    public Result(int status, T data) {
        this.status = status;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
