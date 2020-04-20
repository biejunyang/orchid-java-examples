package com.orchid.examples.security.auth;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT鉴权过滤器
 *
 */
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenHeader = request.getHeader("Authorization");
        if(tokenHeader==null){

        }else{
            // 如果请求头中没有Authorization信息则直接放行了
//            if (tokenHeader == null || !tokenHeader.startsWith(TestJwtUtils.TOKEN_PREFIX)) {
////                chain.doFilter(request, response);
//                return;
//            }
            // 如果请求头中有token，则进行解析，并且设置认证信息
//            SecurityContextHolder.getContext().setAuthentication(getAuthentication(tokenHeader));
            filterChain.doFilter(request, response);
        }

    }



    // 这里从token中获取用户信息并新建一个token
//    private UsernamePasswordAuthenticationToken getAuthentication(String tokenHeader) {
//        String token = tokenHeader.replace(TestJwtUtils.TOKEN_PREFIX, "");
//        String username = TestJwtUtils.getUsername(token);
//        String role = TestJwtUtils.getUserRole(token);
//        if (username != null){
//            return new UsernamePasswordAuthenticationToken(username, null,
//                    Collections.singleton(new SimpleGrantedAuthority(role))
//            );
//        }
//        return null;
//    }
}
