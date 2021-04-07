package com.orchid.example.resouceserver2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//@EnableResourceServer
public class OAuth2ResourceServer2App {



    public static void main(String[] args) {
        SpringApplication.run(OAuth2ResourceServer2App.class, args);
    }

//    @PreAuthorize("hasAnyAuthority('hello')")
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


    @GetMapping("/userInfo")
    public Object userInfo(AbstractAuthenticationToken authenticationToken){
        return authenticationToken;
    }
}
