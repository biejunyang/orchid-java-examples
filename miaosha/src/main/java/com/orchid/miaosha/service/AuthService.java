package com.orchid.miaosha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

//    public R login(LoginVo loginVo){
//        User user=userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, loginVo.getUsername()));
//        if(user==null){
//            return R.error(CodeMessage.LOGIN_USERNAME_ERROR);
//        }
//        String pwd= PasswordUtil.formPwd2Dbpwd(loginVo.getPassword(), user.getSalt());
//        if(!user.getPassword().equals(pwd)){
//            return R.error(CodeMessage.LOGIN_PASSWORD_ERROR);
//        }
//        return R.success();
//    }
}
