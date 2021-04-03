package com.orchid.springboot.swagger.controller;

public class R<T> {
    private Integer code;
    private String messag;
    private T data;

    public R(Integer code, String messag, T data) {
        this.code = code;
        this.messag = messag;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessag() {
        return messag;
    }

    public void setMessag(String messag) {
        this.messag = messag;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
