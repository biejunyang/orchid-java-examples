package com.orchid.examples.security.auth;

import cn.hutool.core.util.ObjectUtil;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;

@Component
public class MyAuthenticationProvide implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authenticationToken=(UsernamePasswordAuthenticationToken)authentication;
        Object username=authenticationToken.getPrincipal();
        if(ObjectUtil.isEmpty(username)){
            throw new BadCredentialsException("用户名不能为空!");
        }

        Object password=authenticationToken.getCredentials();
        if(ObjectUtil.isEmpty(password)){
            throw new BadCredentialsException("密码不能为空!");
        }

        if(!username.equals("admin")){
            throw new BadCredentialsException("用户名不存在!");
        }
        if(!password.equals("123456")){
            throw new BadCredentialsException("密码错误!");
        }
        return new UsernamePasswordAuthenticationToken(username,password,new ArrayList<>());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
