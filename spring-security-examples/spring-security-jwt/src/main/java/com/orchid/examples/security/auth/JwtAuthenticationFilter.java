package com.orchid.examples.security.auth;

import com.orchid.core.http.R;
import com.orchid.web.util.ResponseUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Jwt认证过滤器，处理登录认证
 */
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public JwtAuthenticationFilter() {
        super();
        this.setFilterProcessesUrl("/login");
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        // 从输入流中获取到登录的信息
//        try {
//            LoginUser loginUser = new ObjectMapper().readValue(request.getInputStream(), LoginUser.class);
//            return authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword())
//            );
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
        String username=request.getParameter("username");
        String password=request.getParameter("password");
        Authentication authentication= getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(username, password));
//        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    // 成功验证后调用的方法
    // 如果验证成功，就生成token并返回
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

//        JwtUser jwtUser = (JwtUser) authResult.getPrincipal();
//        System.out.println("jwtUser:" + jwtUser.toString());
//
//        String role = "";
//        Collection<? extends GrantedAuthority> authorities = jwtUser.getAuthorities();
//        for (GrantedAuthority authority : authorities){
//            role = authority.getAuthority();
//        }
//
//        String token = TestJwtUtils.createToken(jwtUser.getUsername(), role);
//        //String token = JwtTokenUtils.createToken(jwtUser.getUsername(), false);
//        // 返回创建成功的token
//        // 但是这里创建的token只是单纯的token
//        // 按照jwt的规定，最后请求的时候应该是 `Bearer token`
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType("application/json; charset=utf-8");
//        String tokenStr = JwtTokenUtils.TOKEN_PREFIX + token;
//        response.setHeader("token",tokenStr);
        ResponseUtil.renderJson(response,R.success(UUID.randomUUID().toString()));

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.getWriter().write("authentication failed, reason: " + failed.getMessage());
    }
}
