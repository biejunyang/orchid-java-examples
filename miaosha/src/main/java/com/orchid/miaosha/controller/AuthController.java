package com.orchid.miaosha.controller;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.orchid.core.Result;
import com.orchid.core.exception.ExceptionBuilder;
import com.orchid.miaosha.enums.MiaoshaResultCode;
import com.orchid.miaosha.entity.User;
import com.orchid.miaosha.service.AuthService;
import com.orchid.miaosha.service.UserService;
import com.orchid.miaosha.util.PasswordUtil;
import com.orchid.miaosha.vo.LoginVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@Controller
@Slf4j
public class AuthController {

    @GetMapping("/loginPage")
    public String loginPage(){
        return "login";
    }


    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @ResponseBody
    public Result<Boolean> doLogin(@Valid LoginVo loginVo, HttpServletResponse response){
        log.info("login info:{}", loginVo.toString());
        authService.login(loginVo, response);
        return Result.success(true);
    }

}
