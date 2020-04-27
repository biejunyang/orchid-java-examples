package com.orchid.examples.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@SpringBootApplication
@RestController
public class JwtSecurityApp {

    public static void main(String[] args){
        SpringApplication.run(JwtSecurityApp.class, args);
    }



    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }



    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/userInfo")
    @ResponseBody
    public Object userInfo(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        return authentication.getPrincipal();
    }

    /**
     * 获取登录后的Principal（需要登录）
     */
    @GetMapping("/getPrincipal")
    @ResponseBody
    public Object getPrincipal(@AuthenticationPrincipal Principal principal){
        return principal;
    }

    /**
     * 获取登录后的UserDetails（需要登录）
     */
    @GetMapping("/getUserDetails")
    @ResponseBody
    public Object getUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
        return userDetails;
    }

}
