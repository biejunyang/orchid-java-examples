package com.security.jwt;


import com.nimbusds.jose.JOSEException;
import com.orchid.core.jwt.JwtTokenUtil;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class JwtTokenTest {


    @Test
    public void test1() throws JOSEException, ParseException {
        String token=JwtTokenUtil.createToken("张三");
        System.out.println(token);

        String subject=JwtTokenUtil.parseSubject(token);

        System.out.println(subject);
    }


    @Test
    public void test2() throws JOSEException, ParseException {
        Map<String, Object> map=new HashMap<>();
        map.put("name", "张三");
        map.put("age", 21);
        map.put("birthday", new Date());
        String token=JwtTokenUtil.createToken(map);
        System.out.println(token);

        Map<String, Object> claims=JwtTokenUtil.parseClaims(token,Map.class);

        System.out.println(claims);
    }

}
