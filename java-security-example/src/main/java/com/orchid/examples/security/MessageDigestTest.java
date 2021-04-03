package com.orchid.examples.security;

import cn.hutool.core.codec.Base64;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MessageDigestTest {

    public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        MessageDigest md5=MessageDigest.getInstance("MD5");

        byte[] bytes=md5.digest("12312".getBytes());
        System.out.println(Base64.encode(bytes));


        bytes=md5.digest("12512".getBytes());
        System.out.println(Base64.encode(bytes));


    }
}
