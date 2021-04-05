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
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;


@RestController
@SpringBootApplication
@EnableResourceServer
public class OAuth2AuthServerApp {


//    @Autowired
//    private TokenStore tokenStore;

    public static void main(String[] args) {
        SpringApplication.run(OAuth2AuthServerApp.class, args);
    }



    @PreAuthorize("hasAuthority('hello')")
//    @PreAuthorize("hasRole('hello')")
    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }




    /**
     * 获取Token对象信息
     * @param token
     * @return
     */
//    @GetMapping("/userInfo")
//    @ResponseBody
//    public Object userInfo(String token){
//        if(ObjectUtil.isNotEmpty(token)){
//            System.out.println(token);
//            return tokenStore.readAuthentication(token);
//        }
//        return null;
//    }

    /**
     * 获取Authentication对象信息
     * @param token
     * @return
     */
    @GetMapping("/userinfo2")
    @ResponseBody
    public Object userinfo(AbstractAuthenticationToken token) {
        System.out.println(token);
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication);
        return token;
    }


    /**
     * 也是Authentication对象信息
     * @param principal
     * @return
     */
    @GetMapping("/userinfo3")
    @ResponseBody
    public Object userinfo3(@AuthenticationPrincipal Principal principal) {
        System.out.println(principal);
        return principal;
    }
}
