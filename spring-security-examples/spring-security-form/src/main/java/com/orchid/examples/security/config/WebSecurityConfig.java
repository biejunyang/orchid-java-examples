package com.orchid.examples.security.config;

import com.orchid.examples.security.auth.MyAuthenticationFailureHandler;
import com.orchid.examples.security.auth.MyAuthenticationProvide;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyAuthenticationProvide myAuthenticationProvide;

    @Autowired
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //自定义认证处理
        auth.authenticationProvider(myAuthenticationProvide);

//        auth.inMemoryAuthentication()
//                .withUser("admin").password("{noop}123456").roles("ADMIN", "USER")
//                .and().withUser("user").password("123456").roles("USER");
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
                .antMatchers("/loginPage").permitAll()
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
                .failureUrl("/login?error")
//                    .failureForwardUrl("/login?error2")
//                    .failureHandler(myAuthenticationFailureHandler)
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
