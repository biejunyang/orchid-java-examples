package com.orchid.examples.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class FormSecurityApp {

    public static void main(String[] args){
        SpringApplication.run(FormSecurityApp.class, args);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

}
