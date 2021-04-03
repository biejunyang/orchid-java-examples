package com.orchid.example.apollo;

import com.orchid.example.apollo.config.TestConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableConfigurationProperties
@SpringBootApplication
@RestController
public class ApolloConfigExampleApp {



    public static void main(String[] args) {
        SpringApplication.run(ApolloConfigExampleApp.class, args);
    }


    @Value("${mytest}")
    private String test;

    @Value("${privatekey1:a}")
    private String privatekey1;

    @Value("${publickey1:b}")
    private String publickey1;


    @Autowired
    private TestConfigProperties testConfigProperties;

    @GetMapping("/test")
    public String test(){
        return this.test+"-"+this.privatekey1+"-"+this.publickey1;
    }

    @GetMapping("/test2")
    public TestConfigProperties test2(){
        return this.testConfigProperties;
    }

}
