package com.orchid.examples.security.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class MyAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

    /**
     * 定义过滤器处理登录请求url
     */
    public MyAuthenticationProcessingFilter() {
        super(new AntPathRequestMatcher("/login", "POST"));
    }


    /**
     * 认证处理
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String code =request.getParameter("code");
        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
        if (code == null) {
            code = "";
        }
        username = username.trim();
        MyAuthenticationToken authRequest = new MyAuthenticationToken(username, password, code);
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
