package com.orchid.examples.repeatedinsert;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.orchid.common.redis.util.RedisUtil;
import com.orchid.examples.repeatedinsert.dao.TestDao;
import com.orchid.examples.repeatedinsert.model.Test;
import com.orchid.examples.repeatedinsert.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

@Slf4j
@SpringBootApplication
@MapperScan({"com.orchid.**.dao"})
@RestController
public class MainApp {

    public static void main(String[] args){
        SpringApplication.run(MainApp.class, args);
        log.info("start serveer");
    }


    @Autowired
    private TestService testService;

    @Autowired
    private RedisUtil redisUtil;

    @PostMapping("/test")
    public Object add(@RequestBody Test test){
        try {
//           1、 一般实现方式、及使用唯一索引方式
//            return testService.add(test);

//            2、使用insert select 语句插入、插入时判断是否存在
//            return testService.add2(test);

//            3、使用redis分布式锁控制并发
            if(redisUtil.lock(test.getName())){//获取锁
                Object result=testService.add(test);
                redisUtil.releaseLock(test.getName());//释放锁
                return result;
            }
            return "数据重复";

        } catch (InterruptedException e) {
            e.printStackTrace();
            return "失败";
        } catch (SQLIntegrityConstraintViolationException e){
            e.printStackTrace();
            return "重复";
        } catch (Exception e) {
            e.printStackTrace();
            return "失败";
        }
    }


}
