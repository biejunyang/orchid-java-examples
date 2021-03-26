package com.orchid.examples.springsecurity;

import cn.hutool.core.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;


@RestController
@SpringBootApplication
public class OAuth2AuthServerApp {


    @Autowired
    private TokenStore tokenStore;

    public static void main(String[] args) {
        SpringApplication.run(OAuth2AuthServerApp.class, args);
    }




    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }


    /**
     * 获取Token对应的信息
     * @param token
     * @return
     */
    @GetMapping("/userInfo")
    @ResponseBody
    public Object userInfo(String token){
        if(ObjectUtil.isNotEmpty(token)){
            return tokenStore.readAuthentication(token);
        }
        return null;
    }

    @GetMapping("/userinfo2")
    @ResponseBody
    public Object userinfo(AbstractAuthenticationToken token) {
        return token.getPrincipal();
    }

}
