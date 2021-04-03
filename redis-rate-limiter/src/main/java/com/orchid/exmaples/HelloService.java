package com.orchid.exmaples;

import org.springframework.stereotype.Service;

@Service
public class HelloService {


    public String hello(){
        return "<h1>hello</h1>";
    }

}
