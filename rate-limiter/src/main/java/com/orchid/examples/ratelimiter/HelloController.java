package com.orchid.examples.ratelimiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class HelloController {

    @Autowired
    private HelloService helloService;

    //时间段
    private long period=5000L;

    //最大请求处理数量
    private long max=5;

    //计数器
    private AtomicLong atomicLong=new AtomicLong();

    private Long lastTime;

    @GetMapping("/hello")
    public Object hello() throws InterruptedException {
        Long count=null;
        synchronized (atomicLong){
            long now=System.currentTimeMillis();
            if(lastTime!=null){
                if(now-lastTime.longValue() >period ){
                    atomicLong.set(0);
                }
            }
            count=atomicLong.incrementAndGet();
            lastTime=now;
        }
        String res=null;
        if(count<=max){
            res=helloService.hello()+count;
        }else{
            res="人太多，请重试!";
        }
        return res;

    }



}
