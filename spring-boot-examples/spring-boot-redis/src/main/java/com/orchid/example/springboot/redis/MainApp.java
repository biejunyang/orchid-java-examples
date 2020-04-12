package com.orchid.example.springboot.redis;

import com.orchid.common.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@SpringBootApplication
@RestController
@Slf4j
public class MainApp {


    @Autowired
    private RedisUtil redisUtil;

    public static void main(String[] args){
        SpringApplication.run(MainApp.class, args);
    }


    private static int count=500;


    @GetMapping("/add")
    public Object add(){
        try{
            if(redisUtil.lock("lock")){
                int num=new Random().nextInt(1000)+1;
                Thread.sleep(1000);
                int old=count;
                count=old+num;
                log.info("#############："+old+"+"+num+"="+count);
                redisUtil.releaseLock("lock");
                return "成功";
            }
            return "并发量过大";
        }catch (Exception ex){
            ex.printStackTrace();
            return "失败";
        }
    }


    @GetMapping("/subtract")
    public Object subtract(){
        try{
            if(redisUtil.lock("lock")){
                int num=new Random().nextInt(1000)+1;
                int old=count;
                if(num>count){
                    log.info("#############："+old+"-"+num+"=负数");
                    return "不能为负数";
                }
                Thread.sleep(1000);
                count=old-num;
                log.info("#############："+old+"-"+num+"="+count);
                redisUtil.releaseLock("lock");
                return "成功";
            }
            return "并发量过大";
        }catch (Exception ex){
            ex.printStackTrace();
            return "失败";
        }
    }

    @GetMapping("/val")
    public Object val(){
        log.info("#############最后的值："+count);
        return count;
    }
}
