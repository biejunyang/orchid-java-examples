package com.orchid.example.springboot.redis;


import com.orchid.common.redis.util.RedisUtil;
import com.orchid.example.springboot.redis.model.Product;
import com.orchid.example.springboot.redis.model.User;
import com.orchid.example.springboot.redis.service.MarketService;
import com.orchid.example.springboot.redis.service.UserService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=MainApp.class)
public class MarketServiceTest {

    @Autowired
    private MarketService marketService;

    @Autowired
    private UserService userService;


//    @Before
    public void init(){
        List<Product> products=new ArrayList<>();
        for(int i = 1; i<=50; i++){
            Double price=new Random().nextDouble() * 100;
            products.add(new Product().setPrice((double) price.intValue())
                    .setProductName("商品"+i));
        }

        for(int i=1; i<=100; i++){
            User user=new User().setId(i).setName("用户"+i)
                    .setFunds((double) new Random().nextInt(100)+50);

            int count=new Random().nextInt(10);
            while(count>0){
                int pid=new Random().nextInt(50);
                Product product = new Product()
                        .setUserId(user.getId())
                        .setProductName(products.get(pid).getProductName())
                        .setPrice(products.get(pid).getPrice());
                if(user.getInventorys().contains(product)){
                    continue;
                }
                user.getInventorys().add(product);
                count--;
            }
            userService.addUser(user);
        }
    }


    @Test
    public void test(){
        int userId=new Random().nextInt(100)+1;
//        int userId=1;
        User user=userService.getUser(userId);
        System.out.println(user);
        System.out.println(user.getInventorys().size());
        for(Object product: user.getInventorys()){
            System.out.println(product);
        }
        System.out.println("上架前商品-----------------------------");
        Set<Object> products=userService.getUserProducts(userId);
        System.out.println(products.size());
        for(Object product: products){
            System.out.println(product);
        }


        int index=new Random().nextInt(user.getInventorys().size());
        marketService.putaway(user.getInventorys().get(index));
        System.out.println("上架后商品+"+user.getInventorys().get(index).getProductName()+"-----------------------------");
        products=userService.getUserProducts(userId);
        for(Object product: products){
            System.out.println(product);
        }

        System.out.println("上架后市场-----------------------------");
        for(Object product: marketService.list()){
            System.out.println(product);
        }
    }


}
