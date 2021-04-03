package com.orchid.example.springboot.redis.service;

import cn.hutool.core.collection.CollectionUtil;
import com.orchid.common.redis.util.RedisUtil;
import com.orchid.example.springboot.redis.model.Product;
import com.orchid.example.springboot.redis.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class MarketService {



    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 上架商品
     * @param product
     */
    public  boolean putaway(Product product){

        //1、监视用户的商品集合
        String inventoryKey="inventory:"+product.getUserId();
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.watch(inventoryKey);

        //判断要上架的商品是否在自己的商品列表中
        if(!redisTemplate.opsForSet().isMember(inventoryKey, product)){
            redisTemplate.unwatch();
            return false;//不在则取消监视
        }

        //事务开始
        redisTemplate.multi();

        //将商品上架到市场有序集合中
        redisTemplate.opsForZSet().add("market", product, product.getPrice());

        //将商品从用户商品集合中移除
        redisTemplate.opsForSet().remove(inventoryKey, product);

        //执行事务
        redisTemplate.exec();
        redisTemplate.setEnableTransactionSupport(false);
        return true;
    }




    /**
     * 购买商品
     * @param product
     * @param userId
     * @return
     */
    public boolean buy(Product product, Integer userId){
        if(product.getUserId().equals(userId))
            return false;
        User user=(User)redisTemplate.opsForValue().get("user:"+userId);
        Double price=redisTemplate.opsForZSet().score("market", product);
//        Set<Object> market=redisTemplate.opsForZSet().reverseRange("market", 0, -1);
        redisTemplate.watch(CollectionUtil.newArrayList("user:"+userId, "market"));

        //判断用户钱是否足够、并检查商品价格是否有改变
        if(user.getFunds().compareTo(product.getPrice())<0
            || !price.equals(product.getPrice())){
            redisTemplate.unwatch();
            return false;
        }
        redisTemplate.setEnableTransactionSupport(true);
        //从市场有序集合中删除
        redisTemplate.opsForZSet().remove("market", product);
        product.setUserId(userId);
        user.getInventorys().add(product);

        //更新到用户商品集合中
        redisTemplate.opsForValue().set("user:"+userId, user);
        redisTemplate.opsForSet().add("inventory:"+userId, product);

        redisTemplate.exec();
        redisTemplate.setEnableTransactionSupport(false);

        return true;

    }


    /**
     * 市场列表
     * @return
     */
    public Set<Object> list(){
        return redisTemplate.opsForZSet().reverseRange("market", 0, -1);
    }
}
