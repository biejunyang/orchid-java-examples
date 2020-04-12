package com.orchid.samples.oauth2client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
@RestController
public class OAuth2ClientApp {

    @Autowired
    private OAuth2RestTemplate oAuth2RestTemplate;


    @Autowired
    private OAuth2ClientContext oAuth2ClientContext;


    public static void main(String[] args) {
        SpringApplication.run(OAuth2ClientApp.class, args);
    }


    @GetMapping("/hello")
    public String hello(String name){
        System.out.println(oAuth2ClientContext);
//        System.out.println(oAuth2ClientContext!=null ? oAuth2ClientContext.getAccessToken().getValue(): null);
        String result= oAuth2RestTemplate.getForObject("http://localhost:8081/hello", String.class);
        return result;
    }


    @GetMapping("/bye")
    public String bye(String name){
        String result= oAuth2RestTemplate.getForObject("http://localhost:8081/bye", String.class);
        return result;
    }


    @GetMapping("/welcome")
    public String welcome(String name){
        System.out.println(oAuth2ClientContext);
//        System.out.println(oAuth2ClientContext!=null ? oAuth2ClientContext.getAccessToken().getValue(): null);
        return "welcome";
    }
}
