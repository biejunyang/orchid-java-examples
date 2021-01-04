package com.orchid.miaosha.controller;

import com.orchid.common.redis.util.RedisUtil;
import com.orchid.core.http.CodeMessage;
import com.orchid.core.http.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

//@RestController
@Controller
public class DemoController {


    @ResponseBody
    @GetMapping("/hello")
    public R hello(){
        return R.success("success", null);
    }


    @ResponseBody
    @GetMapping("/helloError")
    public R helloError(){
//        return R.error(CodeMessage.SERVER_ERROR);
        return R.error("error");
    }

    @GetMapping("/helloPage")
    public String helloPage(Model model){
        model.addAttribute("name", "张三");
        return "hello";
    }


    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/redis/set")
    @ResponseBody
    public R reidsSet(){
        redisUtil.setNx("hello-key", "hello,张三");
        return R.build(true);
    }


}
