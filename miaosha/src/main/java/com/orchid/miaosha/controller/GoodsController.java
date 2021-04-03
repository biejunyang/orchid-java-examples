package com.orchid.miaosha.controller;

import cn.hutool.core.util.StrUtil;
import com.orchid.miaosha.entity.User;
import com.orchid.miaosha.service.AuthService;
import com.orchid.miaosha.vo.LoginVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;



@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    AuthService authService;

//    @GetMapping("/list")
//    public String toList(@CookieValue(value="token", required = false)String cookieToken,
//                         @RequestParam(value="token", required = false)String paramToken, Model model, HttpServletResponse response){
//        if(StrUtil.isEmpty(cookieToken) && StrUtil.isEmpty(paramToken)){
//            return "redirect:/loginPage";
//        }
//        String token=StrUtil.isNotEmpty(paramToken) ? paramToken : cookieToken;
//        User user=authService.getUserByToken(token, response);
//        if(user==null){
//            return "redirect:/loginPage";
//        }
//        model.addAttribute("user", user);
//        return "goods_list";
//    }

    @GetMapping("/list")
    public String toList(User user, Model model){
        if(user==null){
            return "redirect:/loginPage";
        }
        model.addAttribute("user", user);
        return "goods_list";
    }



//    @GetMapping("/detail")
//    public String detail(@CookieValue(value="token", required = false)String cookieToken,
//                         @RequestParam(value="token", required = false)String paramToken, Model model, HttpServletResponse response){
//        if(StrUtil.isEmpty(cookieToken) && StrUtil.isEmpty(paramToken)){
//            return "redirect:/loginPage";
//        }
//        String token=StrUtil.isNotEmpty(paramToken) ? paramToken : cookieToken;
//        User user=authService.getUserByToken(token, response);
//        if(user==null){
//            return "redirect:/loginPage";
//        }
//        model.addAttribute("user", user);
//        return "detail";
//    }


//    @Autowired
//    private AuthService authService;
//
//    @PostMapping("/login")
//    @ResponseBody
//    public boolean doLogin(@Valid LoginVo loginVo, HttpServletResponse response){
//        log.info("login info:{}", loginVo.toString());
//        authService.login(loginVo, response);
//        return true;
//    }

}
