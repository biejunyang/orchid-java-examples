package com.orchid.examples.springsecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@SpringBootApplication
@EnableResourceServer
public class OAuth2AuthServerApp {


//    @Autowired
//    private TokenStore tokenStore;

    public static void main(String[] args) {
        SpringApplication.run(OAuth2AuthServerApp.class, args);
    }



    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }


    /**
     * 获取Token对应的信息
     * @param token
     * @return
     */
//    @GetMapping("/userInfo")
//    @ResponseBody
//    public Object userInfo(String token){
//        if(ObjectUtil.isNotEmpty(token)){
//            return tokenStore.readAuthentication(token);
//        }
//        return null;
//    }

    @GetMapping("/userinfo2")
    @ResponseBody
    public Object userinfo(AbstractAuthenticationToken token) {
        return token;
    }


    @GetMapping("/userinfo3")
    @ResponseBody
    public Object userinfo3() {

        Object val=SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        Object pri=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return pri;
    }
}
