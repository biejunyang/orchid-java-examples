package com.orchid.miaosha.controller;

import com.orchid.core.Result;
import com.orchid.core.exception.ExceptionBuilder;
import com.orchid.miaosha.enums.MiaoshaResultCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {



    @GetMapping(value = "hello")
    public String hello(String name){
        if(StringUtils.isEmpty(name)){
            throw ExceptionBuilder.build(MiaoshaResultCode.LOGIN_PASSWORD_ERROR);
        }
        return "hello,"+name;
    }


    @GetMapping("hello2")
    public Result<String> hello2(String name){
        if(StringUtils.isEmpty(name)){
//            return Result.error("错误");
            return Result.error(MiaoshaResultCode.LOGIN_PASSWORD_ERROR);
        }
        return Result.success(MiaoshaResultCode.SUCCESS, "hello,"+name);
    }

}
