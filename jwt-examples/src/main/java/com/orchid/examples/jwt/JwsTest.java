package com.orchid.examples.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;

import java.security.SecureRandom;
import java.text.ParseException;

public class JwsTest {


    public static void main(String[] args) throws JOSEException, ParseException {
//        testHMACJWS();

        testRSAJWS();
    }


    /**
     * HMAC加密算法签名
     */
    public static void testHMACJWS() throws JOSEException, ParseException {
        //头部
        JWSHeader jwsHeader=new JWSHeader(JWSAlgorithm.HS256);


        //负载：需要安全保护的数据，可以使String，JSONObject、字节流、BASE64URL加密的数据等
        Payload payload=new Payload("hello");

//        Payload payload=new Payload(new JSONObject(new HashMap<>()));

        //签名秘钥：
        byte[] sharedSecret=new byte[32];
        new SecureRandom().nextBytes(sharedSecret);
        JWSSigner jwsSigner=new MACSigner(sharedSecret);


        //创建JWS对象，并对其签名
        JWSObject jwsObject=new JWSObject(jwsHeader, payload);
        jwsObject.sign(jwsSigner);

        //序列化jws对象传输
        String jws=jwsObject.serialize();

        System.out.println(jws);
//        jws=jws.substring(0, jws.length()-1);



        //解析jws字符串
        jwsObject = JWSObject.parse(jws);

        //验签对象
        JWSVerifier verifier = new MACVerifier(sharedSecret);

        if(jwsObject.verify(verifier)){
            String payloadContent=jwsObject.getPayload().toString();
            System.out.println(payloadContent);
        }else{
            System.out.println("签名错误");
        }
    }



    public static void testRSAJWS() throws JOSEException, ParseException {
        //1、创建RSA算法密钥对
        RSAKey rsaJWK=new RSAKeyGenerator(2048)
                .keyID("123").generate();


        //2、创建头部、主要要指定秘钥对的id
        JWSHeader jwsHeader=new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("123").build();

        //3、负载
        Payload payload=new Payload("hello");

        //JWS对象
        JWSObject jwsObject=new JWSObject(jwsHeader, payload);

        //签名对象
        JWSSigner jwsSigner=new RSASSASigner(rsaJWK);


        //数字签名
        jwsObject.sign(jwsSigner);

        String jws=jwsObject.serialize();
        System.out.println(jws);

        //解析jws字符串
        jwsObject = JWSObject.parse(jws);

        //验签对象
        JWSVerifier verifier = new RSASSAVerifier(rsaJWK.toRSAPublicKey());

        if(jwsObject.verify(verifier)){
            String payloadContent=jwsObject.getPayload().toString();
            System.out.println(payloadContent);
        }else{
            System.out.println("签名错误");
        }
    }
}
