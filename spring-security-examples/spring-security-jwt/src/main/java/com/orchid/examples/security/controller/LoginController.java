package com.orchid.examples.security.controller;

import cn.hutool.core.util.ObjectUtil;
import com.nimbusds.jose.JOSEException;
import com.orchid.core.Result;
import com.orchid.core.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class LoginController {


    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Result login(@RequestBody Map<String, Object> userVo) throws JOSEException {
        if(ObjectUtil.isEmpty(userVo.get("username"))){
            return Result.error("用户名不能为空!");
        }
        if(ObjectUtil.isEmpty(userVo.get("password"))){
            return Result.error("密码不能为空!");
        }
        UserDetails userDetails=userDetailsService.loadUserByUsername(userVo.get("username").toString());
        if(userDetails==null){
            return Result.error("用户名不能存在");
        }

        if(!passwordEncoder.matches(userVo.get("password").toString(), userDetails.getPassword())){
            return Result.error("密码错误");
        }
        return Result.success(JwtTokenUtil.createToken(userDetails.getUsername()));
    }
}
