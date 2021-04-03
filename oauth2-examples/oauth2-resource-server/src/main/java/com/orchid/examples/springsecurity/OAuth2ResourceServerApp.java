package com.orchid.examples.springsecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.authserver.AuthorizationServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController
public class OAuth2ResourceServerApp {

    public static void main(String[] args) {
        SpringApplication.run(OAuth2ResourceServerApp.class, args);
    }

    @GetMapping("/hello")
    public String hello(String name){
        return "<h1>hello,"+name+"</h1>";
    }

    @GetMapping("/welcome")
    public String welcome(String name){
        return "<h1>welcome,"+name+"</h1>";
    }

    @GetMapping("/bye")
    public String bye(String name){
        return "<h1>bye,"+name+"</h1>";
    }
}
