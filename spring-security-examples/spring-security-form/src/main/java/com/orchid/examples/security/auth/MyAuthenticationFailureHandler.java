package com.orchid.examples.security.auth;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class MyAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        String msg=null;
        if(e.getClass().equals(BadCredentialsException.class)){
            msg=e.getMessage();
        }else{
            msg="登录失败";
        }
        String requestedWith = request.getHeader("x-requested-with");
        log.info("----------"+request.getHeader("User-Agent"));
        if (requestedWith != null && requestedWith.equalsIgnoreCase("XMLHttpRequest")) {
            Map<String, Object> map=new HashMap<>();
            map.put("code", 103);
            map.put("msg", msg);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(map));
            response.getWriter().flush();
            response.getWriter().close();
        } else {

            this.getRedirectStrategy().sendRedirect(request,response,"/login?msg="+msg);
        }
    }
}
