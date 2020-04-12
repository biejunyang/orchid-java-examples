package com.orchid.examples.springsecurity.config;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserController {

    @GetMapping("/userinfo")
    public String userInfo(@AuthenticationPrincipal Principal principal){
        System.out.println(principal);
        return principal.getName();
    }
}
