package com.orchid.examples.ratelimiter;


import cn.hutool.core.date.DateUtil;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Slf4j
@RestController
public class TestController {

    private TokenRateLimiter limiter=TokenRateLimiter.crate(5, 1);


    private RateLimiter rateLimiter=RateLimiter.create(1);

    @GetMapping("/test")
    public String test(){
        if(limiter.acquire()){
            log.info("以获取到令牌xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx：{}", new Date());
            return "test";
        }else{
//            log.info("未获取到令牌xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx：{}", new Date());
            return "to many requests";
        }
    }


    @GetMapping("/test2")
    public String test2(){

//        log.info("获取到令牌xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx：{},{}", rateLimiter.acquire(),new Date());
//        return "test2";

        if(rateLimiter.tryAcquire()){
            log.info("以获取到令牌xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx：{}", new Date());
            return "test";
        }else{
            log.info("未获取到令牌xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx：{}", new Date());
            return "to many requests";
        }
    }
}
