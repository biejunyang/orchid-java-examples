package com.orchid.examples.security;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.codec.Base64Encoder;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AesTest {

    public static void main(String[] args) throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        String str="hello";
        String aesKey=getKeyAES();
        SecretKey secretKey=loadKeyAes(aesKey);

        //加密
        byte[] sources=encryptAES(str.getBytes(), secretKey);
        System.out.println(Base64.encode(sources));

        //解密
        str=new String(decryptAES(sources, secretKey));
        System.out.println(str);
    }


    /**
     * 生成AES秘钥、并使用Base64编码字符串存储
     * @return
     */
    private static String getKeyAES() throws NoSuchAlgorithmException {
        //获取键的构造器
        KeyGenerator keyGenerator=KeyGenerator.getInstance("AES");

        //设置键键的位数
        keyGenerator.init(128);

        //生成键
        SecretKey secretKey=keyGenerator.generateKey();

        String base64Str= Base64.encode(secretKey.getEncoded());

        return base64Str;
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
