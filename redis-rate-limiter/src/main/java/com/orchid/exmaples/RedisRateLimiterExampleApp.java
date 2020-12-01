package com.orchid.exmaples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;


@SpringBootApplication
@RestController
public class RedisRateLimiterExampleApp {


    public static void main(String[] args) {
        SpringApplication.run(RedisRateLimiterExampleApp.class,args);
    }

}
