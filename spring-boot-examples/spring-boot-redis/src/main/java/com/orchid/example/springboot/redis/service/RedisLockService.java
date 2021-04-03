package com.orchid.example.springboot.redis.service;

import com.orchid.common.redis.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RedisLockService {
    //获取锁等待超时时间3秒
    private final static long WAIT_TIME=10000;

    //锁过期时间(防止线程异常后，长时间占用锁)
    private final static long EXPIRE_TIME=3;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    /**
     * 加锁
     * @param lockname
     * @return
     */
    public boolean lock(String lockname){
        long end=System.currentTimeMillis()+WAIT_TIME;
        while (System.currentTimeMillis() <= end){//等待获取锁超时后退出
            if(redisUtil.set(lockname, UUID.randomUUID().toString())){
                redisUtil.expire(lockname, EXPIRE_TIME);
                return true;
            }else if(redisUtil.ttl(lockname)!=null){
                redisUtil.expire(lockname, EXPIRE_TIME);
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * 释放锁
     * @param lockName
     */
    public boolean releaseLock(String lockName, String identifier){
        while (true){
            redisTemplate.watch(lockName);
            //检查锁有没有被修改
            if(redisTemplate.opsForValue().get(lockName).equals(identifier)){
                redisTemplate.setEnableTransactionSupport(true);
                redisTemplate.multi();
                redisTemplate.delete(lockName);
                redisTemplate.exec();
                redisTemplate.setEnableTransactionSupport(false);
                return true;
            }
            redisTemplate.unwatch();
            break;
        }
        return false;

    }
}
