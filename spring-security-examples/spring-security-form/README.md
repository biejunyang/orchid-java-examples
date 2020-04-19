# spring-securiy-from
> 此 demo 主要演示了 Spring Boot 如何整合 Spring Securitiy框架进行安全认证管理。并介绍了Spring Security的基本认证流程，以及使用表单认证的方式进行认证，自定义认证方式实现。

## 1、pom依赖添加
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-taglibs</artifactId>
</dependency>
```

## 2、Spring Security基本认证流程

### 1、FilterChianProxy(认证入口)：

Spring Security的核心过滤器、Spring Security安全认证的入口，拦截所有的请求根据需要进行安全保护和认证。具体的保护和认证操作委托给内置的一个过滤器链来实现。

### 2、AbstractAuthenticationProcessingFilter(认证过滤器)：
认证过滤器的抽象接口，过滤器包含了一个认证过滤器来实现登录认证，Spring Security表单登录认证的认证过滤器实现为UsernamePasswordAuthenticationFilter，拦截登录认证请求默认为POST"/login"。<br />
实现该过滤器本身并不直接进行认证处理，而是将请求封装成UsernamePasswordAuthenticationToken对象、然后调用设置的AuthenticationManager对象就行认证处理。<br />
认证成功后AuthenticationManager返回一个已认证的AuthenticationManager对象，并保存到认证上下文中，然后然后调用AuthenticationSuccessHandler对象执行认证成功处理。<br />
认证失败则调用AuthenticationFailureHandler对象执行失败处理 
     
### 3、AuthenticationManager(认证管理器)：
认证管理器的作用是接收认证过滤器传递的未认证的Authentication认证信息对象，认证成功则返回一个已认证的Authentication对象。<br />
Spring Security提供的默认实现为ProviderManager。默认他本身也并不直接认证，而是调用实际的认证提供者AuthenticationProvider处理认证
并且认证管理器中可以设置多个AuthenticationProvider进行多重认证。
    
### 4、AuthenticationProvider(认证提供者)：
认证逻辑的实际的执行者，Spring Security默认实现为DaoAuthenticationProvider。他通过UserDetailService对象获取实际用户信息，然后在认证信息就行比对校验。
   
 
## 2、Spring Security 表单认证基本配置

### 代码
```java

@Configuration
@EnableWebSecurity
public class WebSecurityConfig2 extends WebSecurityConfigurerAdapter {
    
    /**
     * 全局安全约束设置
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/favicon.ico");
    }

    
    /**
     * 密码家铭方式管理
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    /**
     * 获取用户信息管理
     */
    @Autowired
    private UserDetailsService userDetailsService;


    /**
     * 认证管理器设置
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //自定义认证处理
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }



    /**安全约束、认证方式设置
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/login").permitAll()
                .anyRequest().authenticated().and()
            .formLogin()
                .loginPage("/login").loginProcessingUrl("/login")
                .usernameParameter("username").passwordParameter("password")
                .defaultSuccessUrl("/welcome.html")
//                .successForwardUrl("/welcome.html")
//                .successHandler(myAuthenticationSuccesHandler)
                .failureUrl("/loginPage2?error")
//                .failureForwardUrl("/login?error2")
//                .failureHandler(myAuthenticationFailureHandler)
                .and()
            .logout().and()
            .csrf().disable()
        ;
    }

}

```
 
### 说明
a、设置认证方式为表单认证，并允许访问登录相关端点服务
    注意：permitAll()允许访问的请求，需设置在authenticated()需要认证的请求之前
    
b、登录相关端点设置
    http.loginPage("/login")//登录页面设置
        .loginProcessingUrl("/login")//登录请求设置,认证过滤器将拦击处理该请求进行认证
        .usernameParameter("username").passwordParameter("password")//表单参数设置
    
c、登录成功处理
    认证成功后，结果由AuthenticationSuccessHandler处理，如：
     http.defaultSuccessUrl("/welcome.html")//登录成功后中定向到该请求
         .successForwardUrl("/welcome.html")//登录成功请求转发到给请求
         .successHandler(new AuthenticationSuccessHandler());//使用自定义认证成功处理器执行处理
         
    注意3中方式任选其一，后者覆盖前者、只有一个有效。Spring Security内置的认证成功处理器：
    
     SimpleUrlAuthenticationSuccessHandler：简单请求重定向处理
     SavedRequestAwareAuthenticationSuccessHandler：跳转到认证之前的那个请求地址
    
d、登录失败处理
    认证失败后，结果由交给AuthenticationFailureHandler处理，如：
    
    http.failureUrl("/login?error1")//登录失败后中定向到该请求
        .failureForwardUrl("/login?error2")//登录失败请求转发到给请求
        .failureHandler(new AuthenticationFailureHandler());//使用自定义认证成功处理器执行处理
    
    3中方式任选其一，后者覆盖前者、只有一个有效。
    在Spring Security内置了几种验证失败处理器：
        DelegatingAuthenticationFailureHandler将AuthenticationException子类委托给不同的AuthenticationFailureHandler，这意味着我们可以为AuthenticationException的不同实例创建不同的行为
        ExceptionMappingAuthenticationFailureHandler根据AuthenticationException的完整类名将用户重定向到特定的URL,内置的异常类包括BadCredentialsException、CaptchaException、AccountExpiredException、LockedException等
        SimpleUrlAuthenticationFailureHandler是默认使用的组件，如果指定，它会将用户重定向到failureUrl;否则，它只会返回401响应
                
e、其他功能如：登录注销设置、禁用csfr
    
4、获取已认证用户信息

   @GetMapping("/userInfo")
   @ResponseBody
   public Object userInfo(){
       Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
       return authentication.getPrincipal();
   }
   
    SecurityContextHolder可以获取到当前请求的安全上线文信息，并从中获取到已认证的认证用户信息。其他获取的Authentication则AuthenticationProvider认证成功之后返回对象。保存在安全上线文中。
    SecurityContext会通过session来维持状态，所以登录后每次请求都可以从session中获取当前用户Authentication，这是系统内部实现的


4、自定义AuthenticationProvider实现自定义认证
    根据spring security默认认证流程来看、UsernamePasswordAuthenticationFilter默认是处理登录请求的过滤器、AuthenticationProvider为实际的认证提供者。
    我们可以自定通过自定这两个组件来实现自定义认证。
    
    自定义认证提供者：
        
        @Component
        public class MyAuthenticationProvide implements AuthenticationProvider {
        
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                Object username=authentication.getPrincipal();
                if(ObjectUtil.isEmpty(username)){
                    throw new BadCredentialsException("用户名不能为空!");
                }
                Object password=authentication.getCredentials();
                if(ObjectUtil.isEmpty(password)){
                    throw new BadCredentialsException("密码不能为空!");
                }
        
                if(!username.equals("admin")){
                    throw new BadCredentialsException("用户名不存在!");
                }
                if(!password.equals("123456")){
                    throw new BadCredentialsException("密码错误!");
                }
                List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        
                UsernamePasswordAuthenticationToken authenticatedToken=new UsernamePasswordAuthenticationToken(username,password,authorities);
                /**
                 * 认证完成后，设置一些详情信息
                 */
                authenticatedToken.setDetails(authentication.getDetails());
                return authenticatedToken;
            }
        
            
            @Override
            public boolean supports(Class<?> aClass) {
                return UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass);
            }
        }
      
    2、使用自定义定义的认证提供者组件
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            //自定义认证处理
            auth.authenticationProvider(myAuthenticationProvide);
        }
        
     
    3、Details自定义参数设置
        认证的入参是一个未认证Authentication，出参是一个已认证Authentication，formLogin的默认Authentication实现是UsernamePasswordAuthenticationToken。
    该认证参数默认可以用来接收输入的用户名和密码参数、若是还有其他的认证参数，则可以使用自定义Details。
    Authentication认证信息中还定义了一个Details字段，可以用来接收自定义的认证参数，默认Details实现为WebAuthenticationDetails类型，该类中包括用户ip和sessionId两个参数。
    
        自定义Details实现,定义需要的认证参数，并从HttpServletRequest中使用参数：
                public class MyWebAuthenticationDetails extends WebAuthenticationDetails {
                
                    private String code;
                
                    public MyWebAuthenticationDetails(HttpServletRequest request) {
                        super(request);
                        this.code=request.getParameter("code");
                    }
                }
                
        设置Authentication认证信息中Detail的实际类型：
        
            http.formLogin()
                .authenticationDetailsSource(new AuthenticationDetailsSource<HttpServletRequest, MyWebAuthenticationDetails>() {
                    @Override
                    public MyWebAuthenticationDetails buildDetails(HttpServletRequest context) {
                        return new MyWebAuthenticationDetails(context);
                    }
                })
    
        
        AuthenticationProvider组件中使用Detail参数，如：
        
        
                @Override
                public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                    Object username=authentication.getPrincipal();
                    if(ObjectUtil.isEmpty(username)){
                        throw new BadCredentialsException("用户名不能为空!");
                    }
                    MyWebAuthenticationDetails details=(MyWebAuthenticationDetails)authentication.getDetails();
                    if(!details.getCode().equals("123")){
                        throw new BadCredentialsException("验证码错误!");
                    }
                    List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            
                    UsernamePasswordAuthenticationToken authenticatedToken=new UsernamePasswordAuthenticationToken(username,password,authorities);
                    /**
                     * 认证完成后，设置一些详情信息
                     */
                    authenticatedToken.setDetails(authentication.getDetails());
                    return authenticatedToken;
                }
                

5、自定义认证过滤器处理登录认证
    Spring Security实现登录认证，最根本是通过核心过滤器链中的认证过滤器来实现的，UsernamePasswordAuthenticationFilter就是默认是处理登录请求的过滤器。
    我们可以添加自定义认证过滤器来处理认证逻辑，从根本上实现自定义认证。
    
    1、实现自定义过滤器，
    
    2、将自定义的过滤器添加到spring security的过滤器链中
        
        注意：自定义添加的过滤器需要根据需要设置认证成功处理器，和认证失败处理器，不能使用failHandler()和successHandler（）设置的。
        并且若是有多个认证过滤器Filter认证时，需要在最后一个认证过滤器认证成功后返回结果，且最终保存在SecurityContext中的Authentication
        对象是第一个个认证过滤器认证成功后返回的Authentication对象。
        
        codeFilter.setContinueChainBeforeSuccessfulAuthentication(true);//验证后继续后认证

        多重认证可以多个定义多个AuthenticationProvider，也可以在过滤器链中添加多个认证过滤器AbstractAuthenticationProcessingFilter联合认证。
        
6、Session管理
    默认用户的认证信息保存在Session对象中，通过检查当前请求的会话对象中的认证信息是否通过认证，来判断用户是否登录。我们可以通过配置来管理Session对象。
    
    超时配置：
        时间、超时处理请求
        
    session并发策略，控制同一个用户创建的Session数量
        http.sessionManagement()
            .maximumSessions(1) //同一个用户的最大并发数
            .maxSessionsPreventsLogin(false)//false之后登录踢掉之前登录,true则不允许之后登录
            .expiredSessionStrategy(new SessionInformationExpiredStrategy() {
            
                //登录被踢掉时的自定义操作
                @Override
                public void onExpiredSessionDetected(SessionInformationExpiredEvent sessionInformationExpiredEvent) throws IOException, ServletException {

                }
            })
    
    分布式Session管理，
        分布式集群环境下，可以使用Redis统一对Session对象管理
            
            <dependency>
                <groupId>org.springframework.session</groupId>
                <artifactId>spring-session-data-redis</artifactId>
            </dependency>
            
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-data-redis</artifactId>
            </dependency>
            <dependency>
                <groupId>org.apache.commons</groupId>
                <artifactId>commons-pool2</artifactId>
            </dependency>
    
    
    开始Redis Session管理配置
        spring:
          redis:
            host: localhost
            port: 6379
          session:
            store-type: redis
            
        
    
SecurityContext：安全上下文信息，用来存储用的认证信息
	实现：

SecurityContextRepository：安全上下文存储仓库，用来存储用户的安全上下文对象SecurityContext
	HttpSessionSecurityContextRespotiory:安全上下文对象存储到HttpSession对象中
	


