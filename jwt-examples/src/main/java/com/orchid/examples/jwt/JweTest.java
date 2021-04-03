package com.orchid.examples.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

public class JweTest {


    public static void main(String[] args) throws JOSEException, ParseException {

        //对JWT负载进行加密
//        testEncryptJWTClaim();

        //对签名的JWT数据加密
        testSignAndEncryptJWTClaim();

    }

    public static void testEncryptJWTClaim() throws JOSEException, ParseException {
        //1、创建RSA算法密钥对
        RSAKey rsaJWK=new RSAKeyGenerator(2048)
                .keyID("123").generate();


        //创建JWE头部:指定加密算法
        JWEHeader jweHeader=new JWEHeader(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A128CBC_HS256);

        // JWT payload
        Date now = new Date();
        JWTClaimsSet jwtClaims = new JWTClaimsSet.Builder()
                .issuer("https://openid.net")
                .subject("alice")
                .audience(Arrays.asList("https://app-one.com", "https://app-two.com"))
                .expirationTime(new Date(now.getTime() + 1000*60*10)) // expires in 10 minutes
                .notBeforeTime(now)
                .issueTime(now)
                .jwtID(UUID.randomUUID().toString())
                .build();

        System.out.println(jwtClaims.toJSONObject());


        //创建加密的jwt对象
        EncryptedJWT encryptedJWT=new EncryptedJWT(jweHeader, jwtClaims);

        //创建加密对象
        JWEEncrypter encrypter=new RSAEncrypter(rsaJWK.toRSAPublicKey());

        //加密
        encryptedJWT.encrypt(encrypter);

        String jwt=encryptedJWT.serialize();

        System.out.println(jwt);

        //解析加密后的jwt数据
        encryptedJWT=EncryptedJWT.parse(jwt);

        //解密对象
        JWEDecrypter decrypter=new RSADecrypter(rsaJWK.toPrivateKey());

        //解密
        encryptedJWT.decrypt(decrypter);

        // Retrieve JWT claims
        System.out.println(encryptedJWT.getJWTClaimsSet().getIssuer());
        System.out.println(encryptedJWT.getJWTClaimsSet().getSubject());
        System.out.println(encryptedJWT.getJWTClaimsSet().getAudience().size());
        System.out.println(encryptedJWT.getJWTClaimsSet().getExpirationTime());
        System.out.println(encryptedJWT.getJWTClaimsSet().getNotBeforeTime());
        System.out.println(encryptedJWT.getJWTClaimsSet().getIssueTime());
        System.out.println(encryptedJWT.getJWTClaimsSet().getJWTID());
    }


    public static void testSignAndEncryptJWTClaim() throws JOSEException, ParseException {
        //创建签名的RSA算法密钥对
        RSAKey rsaJWK=new RSAKeyGenerator(2048)
                .keyID("123").generate();

        //签名jwt对象
        SignedJWT signedJWT=new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("123").build(),
                new JWTClaimsSet.Builder() .subject("alice")
                        .issueTime(new Date())
                        .issuer("https://c2id.com")
                        .build());

        //签名对象
        JWSSigner jwsSigner=new RSASSASigner(rsaJWK);

        //签名
        signedJWT.sign(jwsSigner);


        //创建加密的秘钥对
        RSAKey rsaJWE=new RSAKeyGenerator(2048)
                .keyID("456").generate();


        //创建JWE对象、加密的内容就是签名的jwt对象
        JWEObject jweObject = new JWEObject(
                new JWEHeader.Builder(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A128CBC_HS256)
                        .contentType("JWT") // required to indicate nested JWT
                        .build(),
                new Payload(signedJWT));

        //创建加密对象
        JWEEncrypter encrypter=new RSAEncrypter(rsaJWE.toRSAPublicKey());

        //加密
        jweObject.encrypt(encrypter);

        String jwe=jweObject.serialize();

        System.out.println(jwe);


        //解析加密后的jwe数据
        jweObject=JWEObject.parse(jwe);

        //解密对象
        JWEDecrypter decrypter=new RSADecrypter(rsaJWE.toPrivateKey());

        //解密
        jweObject.decrypt(decrypter);

        //解析出签名的jwt对象
        signedJWT=jweObject.getPayload().toSignedJWT();

        JWSVerifier jwsVerifier=new RSASSAVerifier(rsaJWK.toRSAPublicKey());
        if(signedJWT.verify(jwsVerifier)){

            System.out.println(signedJWT.getJWTClaimsSet().getIssuer());
            System.out.println(signedJWT.getJWTClaimsSet().getSubject());
        }else{
            System.out.println("签名错误");
        }
    }
}
