package com.orchid.examples.ratelimiter;

import org.springframework.stereotype.Service;

@Service
public class HelloService {


    public String hello(){
        return "<h1>hello</h1>";
    }

}
