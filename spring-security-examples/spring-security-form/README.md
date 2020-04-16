1、pom依赖添加

2、Spring Securit默认配置
    默认安全约束配置：
        http
            .authorizeRequests().anyRequest().authenticated().and()
            .formLogin().and()
            .httpBasic();
        说明：默认拦击所有请求路径、使用表单认证和basic认证其中一种
       
       
   默认用户认配置、applicaiton.yml中配置：
      spring:
        security:
          user:
            name: admin
            password: 123456
            roles: AMDIN.USER
            
3、Spring Security认证流程
    1、UsernamePasswordAuthenticationFilter：
        登录认证过滤器，拦截登录认证请求默认为POST"/login"。该过滤器本身并不直接进行认证处理，
        而是将请求封装成UsernamePasswordAuthenticationToken对象、然后调用设置的AuthenticationManager对象就行认证处理，如：
        
    2、AuthenticationManager：
        认证管理器、默认实现为ProviderManager。默认他本身也并不直接认证，而是调用实际的认证提供者AuthenticationProvider，
        认证管理器中可以设置多个AuthenticationProvider进行多重认证。
    
    3、AuthenticationProvider：
        认证提供者进行实际认证处理，默认实现为DaoAuthenticationProvider.DaoAuthenticationProvider
        会调用UserDetailServer.loadUserByUsername(name)，获取用户信息，然后和认证信息就行校验
   
    4、认证完成后处理：
        认证成功则会一个认证状态为已认证的Authentication对象，认证过滤器并将其保存到Session中、
        然后调用AuthenticationSuccessHandler对象执行认证成功处理
        
        认证失败则调用AuthenticationFailureHandler对象执行失败处理
    
3、基本表单认证设置
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
    
    a、设置认证方式为表单认证，并允许访问登录相关端点服务
        
    b、登录相关端点设置
        http.loginPage("/login")//登录页面设置
            .loginProcessingUrl("/login")//登录请求设置,认证过滤器将拦击处理该请求进行认证
            .usernameParameter("username").passwordParameter("password")//表单参数设置
        
    c、登录成功处理
         http.defaultSuccessUrl("/welcome.html")//登录成功后中定向到该请求
             .successForwardUrl("/welcome.html")//登录成功请求转发到给请求
             .successHandler(new AuthenticationSuccessHandler());//使用自定义认证成功处理器执行处理
             
        注意3中方式任选其一，后者覆盖前者、只有一个有效。
        
    d、登录失败处理
        http.failureUrl("/login?error1")//登录失败后中定向到该请求
            .failureForwardUrl("/login?error2")//登录失败请求转发到给请求
            .failureHandler(new AuthenticationFailureHandler());//使用自定义认证成功处理器执行处理
            
        注意3中方式任选其一，后者覆盖前者、只有一个有效。
    e、其他功能如：登录注销设置、禁用csfr
    
    
4、自定义认证


