package com.orchid.miaosha.util;

import cn.hutool.crypto.digest.DigestUtil;

public class PasswordUtil {

    private static final String salt="1282efjc923rkd";


    /**
     * 输入密码第一次md5加密
     * @param inputPwd
     * @return
     */
    public static String inputPwdFormPwd(String inputPwd){
        String str=""+salt.charAt(0)+salt.charAt(2)+inputPwd+salt.charAt(5)+salt.charAt(4);
        return DigestUtil.md5Hex(str);
    }


    /**
     * form表单提交的md5加密密码，进行第二次MD5加密，存入到数据库中
     * @param formPwd
     * @param salt
     * @return
     */
    public static String formPwd2Dbpwd(String formPwd, String salt){
        String str=""+salt.charAt(0)+salt.charAt(2)+formPwd+salt.charAt(5)+salt.charAt(4);
        return DigestUtil.md5Hex(str);
    }


    /**
     * 用户输入密码，两次加密后生成数据库存储的密码
     * @param inputPwd
     * @param dbSalt
     * @return
     */
    public static String inputPwd2Dbpwd(String inputPwd, String dbSalt){
        String formPwd=inputPwdFormPwd(inputPwd);
        String dbPwd=formPwd2Dbpwd(formPwd, dbSalt);
        return dbPwd;
    }






    public static void main(String[] args) {
//        System.out.println(inputPwdFormPwd("123456"));
//        System.out.println(formPwd2Dbpwd(inputPwdFormPwd("123456"), "1282efjc923rkd"));
        System.out.println(inputPwd2Dbpwd("123456", "a1as43534d234"));
    }



}
