package com.orchid.examples.security.auth;

import cn.hutool.core.util.ObjectUtil;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.naming.NameNotFoundException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

@Component
public class MyAuthenticationProvide implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Object username=authentication.getPrincipal();
        if(ObjectUtil.isEmpty(username)){
            throw new BadCredentialsException("用户名不能为空!");
        }
        Object password=authentication.getCredentials();
        if(ObjectUtil.isEmpty(password)){
            throw new BadCredentialsException("密码不能为空!");
        }

        if(!username.equals("admin")){
            throw new BadCredentialsException("用户名不存在!");
        }
        if(!password.equals("123456")){
            throw new BadCredentialsException("密码错误!");
        }

        MyWebAuthenticationDetails details=(MyWebAuthenticationDetails)authentication.getDetails();
        if(ObjectUtil.isEmpty(details.getCode())){
            throw new BadCredentialsException("验证码不能为空!");
        }
        if(!details.getCode().equals("123")){
            throw new BadCredentialsException("验证码错误!");
        }
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        UsernamePasswordAuthenticationToken authenticatedToken=new UsernamePasswordAuthenticationToken(username,password,authorities);
        /**
         * 认证完成后，设置一些详情信息
         */
        authenticatedToken.setDetails(authentication.getDetails());
        return authenticatedToken;
    }


    @Override
    public boolean supports(Class<?> aClass) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
