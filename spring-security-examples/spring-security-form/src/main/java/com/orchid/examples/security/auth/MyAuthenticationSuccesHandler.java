package com.orchid.examples.security.auth;

import com.alibaba.fastjson.JSON;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class MyAuthenticationSuccesHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public MyAuthenticationSuccesHandler() {
        super.setDefaultTargetUrl("/welcome.html");
        super.setAlwaysUseDefaultTargetUrl(false);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String requestedWith = request.getHeader("x-requested-with");
        if (requestedWith != null && requestedWith.equalsIgnoreCase("XMLHttpRequest")) {
            Map<String, Object> map=new HashMap<>();
            map.put("code", 100);
            map.put("msg", "登录成功");
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print( JSON.toJSONString(map));
            response.getWriter().flush();
            response.getWriter().close();
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }

    }
}
