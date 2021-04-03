package com.orchid.examples.security.auth;

import cn.hutool.core.util.ObjectUtil;
import com.orchid.core.Result;
import com.orchid.core.exception.JwtTokenException;
import com.orchid.core.util.JwtTokenUtil;
import com.orchid.web.util.ResponseUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * JWT鉴权过滤器
 */
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().startsWith("/login")) {
            doFilter(request, response, filterChain);
        } else {
            String tokenHeader = request.getHeader("Authorization");
            if (ObjectUtil.isNotEmpty(tokenHeader)) {
                if (tokenHeader.startsWith("Bearer ")) {
                    String token = tokenHeader.substring(7);
                    try {
                        String username = JwtTokenUtil.parseSubject(token);
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        doFilter(request, response, filterChain);
                        return;
                    } catch (JwtTokenException e) {
//                    e.printStackTrace();
                        ResponseUtil.renderJson(response, Result.error(e.getMessage()));
                        return;
                    }
                } else {
                    ResponseUtil.renderJson(response, Result.error("invalid token"));
                }
            } else {
                ResponseUtil.renderJson(response, Result.error("未登录"));
            }
        }
    }

}
