package com.orchid.miaosha.service;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.orchid.common.redis.util.RedisUtil;
import com.orchid.core.exception.ExceptionBuilder;
import com.orchid.miaosha.entity.User;
import com.orchid.miaosha.enums.MiaoshaResultCode;
import com.orchid.miaosha.util.PasswordUtil;
import com.orchid.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    public void login(LoginVo loginVo, HttpServletResponse response){
        if(loginVo==null){
            throw ExceptionBuilder.build(MiaoshaResultCode.SERVER_ERROR);
        }
        User user=userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, loginVo.getUsername()));
        if(user==null){
            throw ExceptionBuilder.build(MiaoshaResultCode.LOGIN_USERNAME_ERROR);
        }
        String pwd= PasswordUtil.formPwd2Dbpwd(loginVo.getPassword(), user.getSalt());
        if(!user.getPassword().equals(pwd)){
            throw ExceptionBuilder.build(MiaoshaResultCode.LOGIN_USERNAME_ERROR);
        }

        String token=UUID.fastUUID().toString();
        addCookie(response, user, token);
    }

    public User getUserByToken(String token, HttpServletResponse response){
        if(StrUtil.isEmpty(token))
            return null;
        User user=(User)redisUtil.get(token);
        addCookie(response, user, token);
        return user;
    }

    private void addCookie(HttpServletResponse response, User user, String token){
        redisUtil.set(token, user, 30);
        Cookie cookie=new Cookie("token", token);
        cookie.setMaxAge(30);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
