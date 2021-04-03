package com.orchid.examples.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Date;

public class JwtTest {


    public static void main(String[] args) throws JOSEException, ParseException {
//        对称加密算法
        testHMACJWT();
//          非对称加密
//        testRSAJWS();
    }


    /**
     * HMAC加密算法签名
     */
    public static void testHMACJWT() throws JOSEException, ParseException {
        //头部
        JWSHeader jwsHeader=new JWSHeader(JWSAlgorithm.HS256);

        //负载
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject("alice")
                .issuer("https://c2id.com")
                .expirationTime(new Date(System.currentTimeMillis() + 60 * 1000))
                .claim("other", new Object())
                .build();


        //签名秘钥：
        byte[] sharedSecret=new byte[32];
        new SecureRandom().nextBytes(sharedSecret);
        JWSSigner jwsSigner=new MACSigner(sharedSecret);


        //创建JWT对象，并对其签名
        SignedJWT signedJWT=new SignedJWT(jwsHeader, claimsSet);
        signedJWT.sign(jwsSigner);

        //序列化jws对象传输
        String jwt=signedJWT.serialize();

        System.out.println(jwt);
//        jws=jws.substring(0, jws.length()-1);



        //解析jws字符串
         signedJWT= SignedJWT.parse(jwt);

        //验签对象
        JWSVerifier verifier = new MACVerifier(sharedSecret);

        if(signedJWT.verify(verifier)){
            String subject=signedJWT.getJWTClaimsSet().getSubject();
            Date exprireDate=signedJWT.getJWTClaimsSet().getExpirationTime();
            String payloadContent=signedJWT.getPayload().toString();
            System.out.println(subject);
            System.out.println(exprireDate);
            System.out.println(payloadContent);
        }else{
            System.out.println("签名错误");
        }
    }

}
