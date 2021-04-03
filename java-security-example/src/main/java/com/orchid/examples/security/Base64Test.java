package com.orchid.examples.security;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;

public class Base64Test {

    public static void main(String[] args) throws IOException {

        //Base64编码：二进制数据编码成可见文本
        String str=encode("hello".getBytes());
        System.out.println(str);

        //Base64解码：将Base64文本解码成二进制数据
        str=new String(decode(str));
        System.out.println(str);

    }

    public static String encode(byte[] bytes){
        BASE64Encoder base64Encoder=new BASE64Encoder();
        return base64Encoder.encode(bytes);
    }

    public static byte[] decode(String base64) throws IOException {
        BASE64Decoder base64Decoder=new BASE64Decoder();
        return base64Decoder.decodeBuffer(base64);
    }
}
