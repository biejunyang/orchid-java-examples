package com.orchid.examples.security;

import cn.hutool.core.codec.Base64;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class RSATest {

    public static void main(String[] args) throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {


//        String str="hello";
//        String aesKey=getKeyAES();
//        SecretKey secretKey=loadKeyAes(aesKey);
//
//        //加密
//        byte[] sources=encryptAES(str.getBytes(), secretKey);
//        System.out.println(Base64.encode(sources));
//
//        //解密
//        str=new String(decryptAES(sources, secretKey));
//        System.out.println(str);
    }


    /**
     * 生成密钥对(公钥和私钥)
     * @return
     */
    private static KeyPair getKeyParie() throws NoSuchAlgorithmException {
        //获取生成秘钥的构造器
        KeyPairGenerator keyGenerator= KeyPairGenerator.getInstance("RSA");

        //设置键的位数
        keyGenerator.initialize(1024);

        //生成
        return  keyGenerator.generateKeyPair();
    }


    /**
     * 加载秘钥字符串，生成秘钥对象
     * @param keyStr
     * @return
     */
    private static SecretKey loadKeyAes(String keyStr){
        SecretKey secretKey=new SecretKeySpec(Base64.decode(keyStr), "AES");
        return secretKey;
    }


    /**
     * 加密
     * @param source
     * @param secretKey
     * @return
     */
    public static byte[] encryptAES(byte[] source, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher=Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(source);
    }

    /**
     * 解密
     * @param source
     * @param secretKey
     * @return
     */
    public static byte[] decryptAES(byte[] source, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher=Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(source);
    }
}
