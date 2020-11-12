package com.orchid.example.apollo.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "")
public class TestConfigProperties {

    private String mytest;

    private String privatekey1;

    private String publickey1;

}
