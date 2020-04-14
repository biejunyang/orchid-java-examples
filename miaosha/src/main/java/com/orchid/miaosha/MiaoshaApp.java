package com.orchid.miaosha;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"com.orchid.**.dao"})
public class MiaoshaApp {


    public static void main(String[] args){
        SpringApplication.run(MiaoshaApp.class, args);
    }
}
