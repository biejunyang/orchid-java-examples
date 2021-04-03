package com.orchid.examples.security.controller;

import cn.hutool.core.util.ObjectUtil;
import com.nimbusds.jose.JOSEException;
import com.orchid.core.http.R;
import com.orchid.core.jwt.JwtTokenUtil;
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
    public R login(@RequestBody Map<String, Object> userVo) throws JOSEException {
        if(ObjectUtil.isEmpty(userVo.get("username"))){
            return R.error("用户名不能为空!", null);
        }
        if(ObjectUtil.isEmpty(userVo.get("password"))){
            return R.error("密码不能为空!", null);
        }
        UserDetails userDetails=userDetailsService.loadUserByUsername(userVo.get("username").toString());
        if(userDetails==null){
            return R.error("用户名不能存在", null);
        }

        if(!passwordEncoder.matches(userVo.get("password").toString(), userDetails.getPassword())){
            return R.error("密码错误", null);
        }
        return R.success(JwtTokenUtil.createToken(userDetails.getUsername()));
    }
}
