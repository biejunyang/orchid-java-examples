package com.orchid.supply;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.orchid.supply.dao")
public class SupplyApp {

    public static void main(String[] args){
        SpringApplication.run(SupplyApp.class, args);
    }
}
