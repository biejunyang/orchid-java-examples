package com.orchid.examples.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("admin").password("{noop}123456").roles("ADMIN", "USER")
                .and().withUser("user").password("123456").roles("USER");
    }

    /**
     * 1、需要认证的请求设置
     * 2、认证端点设置
     *      GET     /login：自定义登录页面设置
     *      POST    /login：用户登录校验端点、使用spring security默认提供的认证端点时、需要禁用csrf功能
     *      GET     /login?myerror：登录失败端点、默认为/login?error
     * 3、登录成功处理
     *    AuthenticationSuccessHandler
     *     defaultSuccessUrl():默认登录成功后将重定向到之前访问的页面、否则将重定向到此设置的页面
     *     successForwardUrl()：请求转发到指定请求
     * 4、登录失败处理
     * 3、其他功能如：注销、记住
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                .formLogin().permitAll()
                    .loginPage("/login").loginProcessingUrl("/login")
                    .usernameParameter("username").passwordParameter("password")
                    .defaultSuccessUrl("/welcome.html")
//                    .successForwardUrl("/welcome.html")
//                    .successHandler(new AuthenticationSuccessHandler() {
//                        @Override
//                        public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
//
//                        }
//                    })
                    .failureUrl("/login?error1")
//                    .failureForwardUrl("/login?error2")
//                    .failureHandler(new AuthenticationFailureHandler() {
//                        @Override
//                        public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
//
//                        }
//                    })
                    .and()
                .logout().and()
                .csrf().disable()
        ;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
