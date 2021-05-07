package com.orchid.examples.security.config;

import com.orchid.examples.security.auth.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyAuthenticationProvide myAuthenticationProvide;

    @Autowired
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;

    @Autowired
    private MyAuthenticationSuccesHandler myAuthenticationSuccesHandler;



    /**
     * 用户认证管理
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //自定义认证处理
        auth.authenticationProvider(myAuthenticationProvide);
//        auth.inMemoryAuthentication()
//                .withUser("admin").password("{noop}123456").roles("ADMIN", "USER")
//                .and().withUser("user").password("{noop}123456").roles("USER");
    }



    /**
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/login").permitAll()
                .anyRequest().authenticated()
//                .anyRequest().access("@authorityService.hasPermission(request,authentication)")
                .and()
            .formLogin()
                .loginPage("/login")
//                .loginProcessingUrl("/login")
//                .usernameParameter("username").passwordParameter("password")
////                .defaultSuccessUrl("/welcome.html")
////                    .successForwardUrl("/welcome.html")
//                .successHandler(myAuthenticationSuccesHandler)
////                .failureUrl("/loginPage2?error")
////                .failureForwardUrl("/login?error2")
//                .failureHandler(myAuthenticationFailureHandler)
//                .authenticationDetailsSource(new AuthenticationDetailsSource<HttpServletRequest, MyWebAuthenticationDetails>() {
//                    @Override
//                    public MyWebAuthenticationDetails buildDetails(HttpServletRequest context) {
//                        return new MyWebAuthenticationDetails(context);
//                    }
//                })
                .and()
            .logout()
                .logoutSuccessUrl("/login")
                .permitAll().and()
            .csrf().disable()
            .sessionManagement()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .expiredSessionStrategy(new SessionInformationExpiredStrategy() {
                    @Override
                    public void onExpiredSessionDetected(SessionInformationExpiredEvent sessionInformationExpiredEvent) throws IOException, ServletException {

                    }
                }).and().and()
            .exceptionHandling()
                .authenticationEntryPoint((httpServletRequest, httpServletResponse, e) -> {

                })
                .accessDeniedHandler((httpServletRequest, httpServletResponse, e) -> System.out.println("xxxxxxxxxxxxxxxxxxxxxx"))
            ;

//        CodeAuthenticationProcessingFilter codeFilter=new CodeAuthenticationProcessingFilter();
//        codeFilter.setAuthenticationManager(this.authenticationManagerBean());
//
//        //设置认证失败处理器
//        codeFilter.setAuthenticationFailureHandler(myAuthenticationFailureHandler);
//
//        //设置认证成功处理器
//        codeFilter.setContinueChainBeforeSuccessfulAuthentication(true);//验证后继续后认证

//        codeFilter.setContinueChainBeforeSuccessfulAuthentication(false);
//        codeFilter.setAuthenticationSuccessHandler(myAuthenticationSuccesHandler);
//        http.addFilterAt(codeFilter, UsernamePasswordAuthenticationFilter.class);


        MyAuthenticationProcessingFilter myAuthenticationProcessingFilter=new MyAuthenticationProcessingFilter();
        myAuthenticationProcessingFilter.setAuthenticationManager(this.authenticationManagerBean());
        myAuthenticationProcessingFilter.setAuthenticationSuccessHandler(myAuthenticationSuccesHandler);
        myAuthenticationProcessingFilter.setAuthenticationFailureHandler(myAuthenticationFailureHandler);
        myAuthenticationProcessingFilter.setAuthenticationDetailsSource(new AuthenticationDetailsSource<HttpServletRequest, MyWebAuthenticationDetails>() {
            @Override
            public MyWebAuthenticationDetails buildDetails(HttpServletRequest context) {
                return new MyWebAuthenticationDetails(context);
            }
        });
        http.addFilterBefore(myAuthenticationProcessingFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/favicon.ico");
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
