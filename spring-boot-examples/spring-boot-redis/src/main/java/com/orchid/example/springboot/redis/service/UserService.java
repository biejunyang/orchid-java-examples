package com.orchid.example.springboot.redis.service;

import com.github.dozermapper.core.Mapper;
import com.orchid.example.springboot.redis.model.Product;
import com.orchid.example.springboot.redis.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class UserService {


    @Autowired
    private Mapper mapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 添加用户
     * @param user
     */
    public void addUser(User user){
//        Map<String, Object> map=new HashMap<>();
//        mapper.map(user, map);
        redisTemplate.opsForValue().set("user:"+user.getId(), user);
        for(Product product: user.getInventorys()){
            redisTemplate.opsForSet().add("inventory:"+user.getId(), product);
        }
    }


    public User getUser(int userId){
        Object val=redisTemplate.opsForValue().get("user:"+userId);
        return val!=null ? (User)val : null;
    }

    public Set<Object> getUserProducts(int userId){
        Set<Object> products=redisTemplate.opsForSet().members("inventory:"+userId);
        return products;
    }
}
