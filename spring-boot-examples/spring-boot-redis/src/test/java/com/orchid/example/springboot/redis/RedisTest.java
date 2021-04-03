package com.orchid.example.springboot.redis;


import com.orchid.common.redis.util.RedisUtil;
import com.orchid.example.springboot.redis.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=MainApp.class)
public class RedisTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ValueOperations<String, Object> valueOperations;

    @Test
    public void test(){

//        stringRedisTemplate.opsForValue().set("hello", "zangsan");
//        redisTemplate.opsForValue().set("100", 100);
//        redisTemplate.opsForValue().set(100, 100);
//        redisTemplate.opsForValue().set(100.34, 100.34);
//        redisTemplate.opsForValue().set("time", new Date());


        User user1=new User(1, "张三", 20, 23.54, 12.0, new Date(), new Date(), new ArrayList<>());
        User user2=new User(2, "李四", 20, 23.54, 12.0, new Date(), new Date(), new ArrayList<>());

//        1、redis 字符串结构数据添加
        redisUtil.set("user:1", user1);

//        2、redis 集合数据添加
        redisUtil.sSet("userSet:", user1, user2);

//        3、redis 列表数据添加
        redisUtil.lSet("userList", user1);
        redisUtil.lSet("userList", user2);

//        4、redis 散列数据添加
        redisUtil.hset("userhash", "user", user1);
        redisUtil.hset("userhash", "enjoy", new String[]{"足球","篮球"});

//        5、redis 有序集合
        redisUtil.zAdd("userzset", user1, user1.getId().doubleValue());
        redisUtil.zAdd("userzset", user2, user2.getId().doubleValue());


        redisUtil.set("name", "张三");
//        redisTemplate.opsForValue().set("user:3", user);
//        redisTemplate.opsForValue().set(user,"userxxxx");

//        Object value=redisTemplate.opsForValue().get("hello");
//        System.out.println(value);
//        System.out.println(value.getClass());

//        stringRedisTemplate.opsForValue().set("aaa", "bbb");
//        redisUtil.set("bbb", "xxxxxx");
//        redisTemplate.opsForValue().set("ccc", "acccc");
//        valueOperations.set("user", user);
//        Object value=redisUtil.get("user");
//        System.out.println(value);
//        System.out.println(value.getClass());
//        redisTemplate.delete("hello");
//        redisTemplate.delete(1);
    }


    /**
     * redis 事务
     */

    @Test
    public void test2(){
        //开启事务支持
        redisTemplate.setEnableTransactionSupport(true);
//        redisTemplate.opsForValue().set("a", "aaa");

        redisTemplate.multi();//事务开始
        redisTemplate.opsForValue().set("a", "aaa");
        redisTemplate.opsForValue().set("b", "bbb");
        redisTemplate.opsForValue().set("c", "ccc");

        redisTemplate.exec();//事务结束
    }


    @Test
    public void test3(){
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.watch("a");
        redisTemplate.multi();
        redisTemplate.opsForValue().set("a", "aaaa");
        redisTemplate.opsForValue().set("b", "bbbb");
        redisTemplate.delete("c");
        List<Object> vals=redisTemplate.exec();
        redisTemplate.setEnableTransactionSupport(false);
//        System.out.println(redisTemplate.opsForValue().get("a"));
//        System.out.println(redisTemplate.opsForValue().get("b"));
    }


    @Test
    public void test4(){
        Long val=redisTemplate.execute((RedisCallback<Long>) con ->{
            Long time=con.ttl("a".getBytes());
            con.close();
            return time;
        });
        System.out.println(val);

    }
}
