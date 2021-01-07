package com.orchid.miaosha.controller;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.orchid.core.exception.ExceptionBuilder;
import com.orchid.miaosha.enums.MiaoshaResultCode;
import com.orchid.miaosha.entity.User;
import com.orchid.miaosha.service.UserService;
import com.orchid.miaosha.util.PasswordUtil;
import com.orchid.miaosha.vo.LoginVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;


@Controller
@Slf4j
public class AuthController {

    @GetMapping("/loginPage")
    public String loginPage(){
        return "login";
    }


    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @ResponseBody
    public String doLogin(@Valid LoginVo loginVo){
        log.info("login info:{}", loginVo.toString());
        User user=userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, loginVo.getUsername()));

        if(user==null){
            throw ExceptionBuilder.build(MiaoshaResultCode.LOGIN_USERNAME_ERROR);
        }
        String pwd=PasswordUtil.formPwd2Dbpwd(loginVo.getPassword(), user.getSalt());
        if(!user.getPassword().equals(pwd)){
            throw ExceptionBuilder.build(MiaoshaResultCode.LOGIN_USERNAME_ERROR);
        }
        return UUID.fastUUID().toString();
    }

}
