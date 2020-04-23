package com.orchid.example.springboot.template;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@Controller
public class SpringBootTemplateApp {

    public static void main(String[] args){
        SpringApplication.run(SpringBootTemplateApp.class);
    }

    @GetMapping({"","/","/index"})
    public String index(Model model){
        Map<String, Object> user=new HashMap<>();
        user.put("id", 1);
        user.put("name", "张三");
        user.put("age", 30);
        user.put("createTime", new Date());
        model.addAttribute("user", user);
        model.addAttribute("name", "zhagnsan");
        return "index";
    }


}
