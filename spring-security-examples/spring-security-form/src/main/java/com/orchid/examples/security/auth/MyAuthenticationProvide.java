package com.orchid.examples.security.auth;

import cn.hutool.core.util.ObjectUtil;
import com.orchid.examples.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.naming.NameNotFoundException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

@Component
public class MyAuthenticationProvide implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        MyAuthenticationToken token=(MyAuthenticationToken)authentication;
        Object username=token.getPrincipal();
        if(ObjectUtil.isEmpty(username)){
            throw new BadCredentialsException("用户名不能为空!");
        }
        Object password=token.getCredentials();
        if(ObjectUtil.isEmpty(password)){
            throw new BadCredentialsException("密码不能为空!");
        }
        String code=token.getCode();
        if(ObjectUtil.isEmpty(password)){
            throw new BadCredentialsException("验证码不能为空!");
        }
//        if(!username.equals("admin")){
//            throw new BadCredentialsException("用户名不存在!");
//        }
//        if(!password.equals("123456")){
//            throw new BadCredentialsException("密码错误!");
//        }
        if(!code.equals("123")){
            throw new BadCredentialsException("验证码错误!");
        }

        UserDetails userDetails =  userService.loadUserByUsername(username.toString());
        if(userDetails==null){
            throw new BadCredentialsException("用户名不存在!");
        }

        if(!passwordEncoder.matches(password.toString(), userDetails.getPassword())){
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
        return MyAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
