package com.orchid.examples.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.security.SecureRandom;
import java.text.ParseException;
import java.util.*;

public class Jwtkest {


    public static void main(String[] args) throws JOSEException, ParseException {

        Map<String, String> map=createJWT();

        RSAKey jwk=(RSAKey)JWK.parse(map.get("jwk"));

        SignedJWT jwt=SignedJWT.parse(map.get("jwt"));

        JWSVerifier jwsVerifier=new RSASSAVerifier(jwk.toRSAPublicKey());
        if(jwt.verify(jwsVerifier)){
            System.out.println(jwt.getJWTClaimsSet().getSubject());
        }else{
            System.out.println("验签失败");
        }

    }





    public static Map<String, String> createJWT() throws JOSEException {
        String keyId=UUID.randomUUID().toString();
        RSAKey jwk = new RSAKeyGenerator(2048)
                .keyUse(KeyUse.SIGNATURE) // indicate the intended use of the key
                .keyID(keyId) // give the key a unique ID
                .generate();

        SignedJWT signedJWT=new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(keyId).build(),
                new JWTClaimsSet.Builder() .subject("alice")
                        .issueTime(new Date())
                        .issuer("https://c2id.com")
                        .build());

        JWSSigner jwsSigner=new RSASSASigner(jwk);
        signedJWT.sign(jwsSigner);

        System.out.println(jwk);
        System.out.println(jwk.toPublicJWK());
        Map<String, String> map=new HashMap<>();
        map.put("jwt", signedJWT.serialize());
        map.put("jwk", jwk.toPublicJWK().toString());
        return map;
    }

}
