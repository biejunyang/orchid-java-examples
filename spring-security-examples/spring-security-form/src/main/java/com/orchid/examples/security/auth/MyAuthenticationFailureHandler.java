package com.orchid.examples.security.auth;

import com.alibaba.fastjson.JSON;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {

        response.sendRedirect("/loginPage");

//        String requestedWith = request.getHeader("x-requested-with");
//        if (requestedWith != null && requestedWith.equalsIgnoreCase("XMLHttpRequest")) {
//            Map<String, Object> map=new HashMap<>();
//            map.put("code", 103);
//            map.put("msg", e.getMessage());
//            response.setContentType("application/json;charset=utf-8");
//            response.getWriter().print(JSON.toJSONString(map));
//            response.getWriter().flush();
//            response.getWriter().close();
//        } else {
//            response.sendRedirect("/login?error");
//        }
    }
}
