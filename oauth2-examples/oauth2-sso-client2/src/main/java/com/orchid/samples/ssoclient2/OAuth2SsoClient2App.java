package com.orchid.samples.ssoclient2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class OAuth2SsoClient2App {

    public static void main(String[] args) {
        SpringApplication.run(OAuth2SsoClient2App.class, args);
    }


    @GetMapping("/hello")
    public String hello(String name){
        return "<h1>hello!</hello>";
    }

}
