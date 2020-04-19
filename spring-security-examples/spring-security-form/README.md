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
   
 
## 3、Spring Security 表单认证基本配置

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
   ```注意：permitAll()允许访问的请求，需设置在authenticated()需要认证的请求之前```
    
b、登录相关端点设置
```java    
    http.loginPage("/login")//登录页面设置
        .loginProcessingUrl("/login")//登录请求设置,认证过滤器将拦击处理该请求进行认证
        .usernameParameter("username").passwordParameter("password")//表单参数设置
```

c、登录成功处理
  
    认证成功后，结果由AuthenticationSuccessHandler处理，如：
     http.defaultSuccessUrl("/welcome.html")//登录成功后中定向到该请求
         .successForwardUrl("/welcome.html")//登录成功请求转发到给请求
         .successHandler(new AuthenticationSuccessHandler());//使用自定义认证成功处理器执行处理
         
    注意3中方式任选其一，后者覆盖前者、只有一个有效。Spring Security内置的认证成功处理器：
    
     SimpleUrlAuthenticationSuccessHandler：简单请求重定向处理
     SavedRequestAwareAuthenticationSuccessHandler：跳转到认证之前的那个请求地址
    
d、登录失败处理
```
    认证失败后，结果由交给AuthenticationFailureHandler处理，如：
    http.failureUrl("/login?error1")//登录失败后中定向到该请求
        .failureForwardUrl("/login?error2")//登录失败请求转发到给请求
        .failureHandler(new AuthenticationFailureHandler());//使用自定义认证成功处理器执行处理
    
    3中方式任选其一，后者覆盖前者、只有一个有效。
    在Spring Security内置了几种验证失败处理器：
        DelegatingAuthenticationFailureHandler将AuthenticationException子类委托给不同的AuthenticationFailureHandler，这意味着我们可以为AuthenticationException的不同实例创建不同的行为
        ExceptionMappingAuthenticationFailureHandler根据AuthenticationException的完整类名将用户重定向到特定的URL,内置的异常类包括BadCredentialsException、CaptchaException、AccountExpiredException、LockedException等
        SimpleUrlAuthenticationFailureHandler是默认使用的组件，如果指定，它会将用户重定向到failureUrl;否则，它只会返回401响应
```            
e、其他功能如：登录注销设置、禁用csfr
    
## 4、获取已认证用户信息
```java  
@GetMapping("/userInfo")
@ResponseBody
public Object userInfo(){
   Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
   return authentication.getPrincipal();
}

/**
 * 获取登录后的Principal（需要登录）
 */
@GetMapping("/getPrincipal")
@ResponseBody
public Object getPrincipal(@AuthenticationPrincipal Principal principal){
    return principal;
}

/**
 * 获取登录后的UserDetails（需要登录）
 */
@GetMapping("/getUserDetails")
@ResponseBody
public Object getUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
    return userDetails;
}
```
    SecurityContextHolder可以获取到当前请求的安全上线文信息，并从中获取到已认证的认证用户信息。其他获取的Authentication则AuthenticationProvider认证成功之后返回对象。保存在安全上线文中。
    SecurityContext会通过session来维持状态，所以登录后每次请求都可以从session中获取当前用户Authentication，这是系统内部实现的


## 5、自定义认证

根据认证流程来看、Spring Security默认使用UsernamePasswordAuthenticationFilter过滤器来处理登录认证请求、间接调用DaoAuthenticationProvider认证提供者来实现认证逻辑，所以我们主要可以通过通过自定这两个组件来实现自定义认证。
    
### 自定义AuthenticationProvider认证提供者
1、创建认证提供者
```java
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

    /**
     * 认证提供者支持的Authentication对象
    **/
    @Override
    public boolean supports(Class<?> aClass) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
```
   
2、使用自定义的认证提供者组件

```java
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    //自定义认证处理
    auth.authenticationProvider(myAuthenticationProvide);
}
```

### 自定义认证过滤器
1、定义认证过滤器

```认证过滤器的抽象父类为AbstractAuthenticationProcessingFilter。实现中我们也可以直接继承内置的UsernamePasswordAuthenticationFilter过滤器，使用其默认的功能，并对其进行扩展。```

```java
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
```
    注意：Sring Security设计中，认证过滤器执行封装了Authentication认证对象，然后调用认证管理器进行认证。自定义认证时可以延用此种设计，也可以直接在认证过滤器中完成认证逻辑。  ````    

2、将自定义的认证过滤器添加到过滤器链中
```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    
    //自定义的认证过滤器
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
    
    http
        .authorizeRequests()
            .antMatchers("/login").permitAll()
            .anyRequest().authenticated()
            .and()
        .formLogin()
            .loginPage("/login")
            .and()
        .logout().and()
        .csrf().disable()
        .addFilterBefore(myAuthenticationProcessingFilter, UsernamePasswordAuthenticationFilter.class);
    ;
}
```
    注意：
    a、使用http.addFilterBefore()、http.addFilterAfter()、http.addFilterAt()可以将自定义过滤器添加到过滤器链中的某个过滤器之前或者之后。
    b、即使我们自定义的认证过滤器不调用认证管理器执行认证，而且直接在认证过滤器中实现，也需要给我们的认证过滤器设置指定认证管理器对象
    c、给认证过滤器设置认证成功/失败处理器，否则使用默认的。并且http.formLogin().loginPage("/login").successHandler().failureHandler()中定义的设置是给UsernamePasswordAuthenticationFilter的，自定义的认真过滤器并不沿用。
    
### 自定义Authentication认证对象
认证过滤器拦截到认证请求后解析请求，并包装成一个认证Authentication对象，交给认证管理器处理。认证管理器则调用认证提供者来处理认证，调用认证提供者时会判断认证提供者是否支持认证该Authentication对象，如：
````  /**
          * 认证提供者支持的Authentication对象
         **/
         @Override
         public boolean supports(Class<?> aClass) {
             return UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass);
         }
````

1、定义认证对象，实现Authentication接口，一般可以对内置的Authentication实现进行扩展，如：
```java
@Data
public class MyAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private String code;

    public MyAuthenticationToken(Object principal, Object credentials, String code) {
        super(principal, credentials);
        this.code=code;
    }
    
    public MyAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
    
}
```

2、在认证过滤器中使用
```java
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
```
### 自定义Details参数设置
认证的入参是一个未认证Authentication，出参是一个已认证Authentication，formLogin的默认Authentication实现是UsernamePasswordAuthenticationToken。该认证参数默认可以用来接收输入的用户名和密码参数、若是还有其他的认证参数，则可以使用自定义Details<br/>。
Authentication认证信息中还定义了一个Details字段，可以用来接收自定义的认证参数，默认Details实现为WebAuthenticationDetails类型，该类中包括用户ip和sessionId两个参数。
    
1、自定义Details实现,定义需要的认证参数，并从HttpServletRequest中解析参数：
``` java
public class MyWebAuthenticationDetails extends WebAuthenticationDetails {

    private String code;

    public MyWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        this.code=request.getParameter("code");
    }
}
```         
       
2、设置Authentication认证信息中Detail的实际类型：

```java
http.formLogin()
    .authenticationDetailsSource(new AuthenticationDetailsSource<HttpServletRequest, MyWebAuthenticationDetails>() {
        @Override
        public MyWebAuthenticationDetails buildDetails(HttpServletRequest context) {
            return new MyWebAuthenticationDetails(context);
        }
    })
```
    
       
3、AuthenticationProvider组件中使用Detail参数，如：

```java
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
                
```

## 6、多重认证
````有两种方式实现多重认证：
    a、过滤器链中添加多个认证过滤器：添加多个认证过滤器认证时要确定好过滤器的顺序，并且开启多级认证：
         codeFilter.setContinueChainBeforeSuccessfulAuthentication(true);//验证后继续后认证
        上一个过滤器认证完成后，继续向下认证。并且注意要保证所有的过滤器都认证成功才执行认证成功处理
            
    b、在AuthenticationManager中添加多个认证提供者，如：
        auth.authenticationProvider(myAuthenticationProvide).authenticationProvider(myAuthenticationProvide2);
````

## 7、Session管理

默认用户的认证信息保存在Session对象中，通过检查当前请求的会话对象中的认证信息是否通过认证，来判断用户是否登录。我们可以通过配置来管理Session对象。
    
1、Session超时配置：
```yaml
server:
  servlet:
    session:
      timeout: 30m #超时配置、默认30分钟
```
```java
http
    .sessionManagement()
        .invalidSessionUrl("/login")//session超时跳转页面
        .invalidSessionStrategy(new InvalidSessionStrategy() {
            @Override
            public void onInvalidSessionDetected(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
                
            }
        })//session超时后处理
;
```
        
2、session并发策略，控制同一个用户创建的Session数量
        http.sessionManagement()
            .maximumSessions(1) //同一个用户的最大并发数
            .maxSessionsPreventsLogin(false)//false之后登录踢掉之前登录,true则不允许之后登录
            .expiredSessionStrategy(new SessionInformationExpiredStrategy() {
            
                //登录被踢掉时的自定义操作
                @Override
                public void onExpiredSessionDetected(SessionInformationExpiredEvent sessionInformationExpiredEvent) throws IOException, ServletException {

                }
            })
    
3、分布式Session管理，

分布式集群环境下，可以使用Redis统一对Session对象管理

1、pom.xml依赖
```xml
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
```

2、开始Redis Session管理配置
```yaml
spring:
  redis:
    host: localhost
    port: 6379
  session:
    store-type: redis
```          
        
  
## 8、权限控制
Spring Security提供了默认的权限控制功能，需要预先分配给用户特定的权限，并指定各项操作执行所要求的权限。用户请求执行某项操作时，Spring Security会先检查用户所拥有的权限是否符合执行该项操作所要求的权限，如果符合，才允许执行该项操作，否则拒绝执行该项操作。

### 方法级权限控制：
1、开启权限方法级权限控制:```@EnableGlobalMethodSecurity(prePostEnabled = true)```

2、使用注解控制方法的权限：
````
@PreAuthorize(spel)：在方法执行之前进行权限验证
@PostAuthorize(spel)：在方法执行之后进行权限验证
@PreFilter(spel)：在方法执行之前对方法集合类型的参数进行过滤
@PostFilter(spel)：在方法执行之后对方法返回的结合类型值进行过滤
````    

3、权限控制Spring EL表达式
上面的限控制注解中，Spring Security使用Spring EL权限验证表达式来指定访问URL或方法所需要的权限，权限验证表达式返回结果为true，则表示用户拥有访问该URL或方法的权限，如果返回结果为false，则表示没有权限。<br/>

| 表达式 | 描述 |
| ----- | --- |
| hasRole([role])| 当前用户是否拥有指定角色。                                           |
| hasAnyRole([role1,role2])| 多个角色是一个以逗号进行分隔的字符串。如果当前用户拥有指定角色中的任意一个则返回true。|  

注意：权限权限名称需要已"ROLE_"开头
```` 


##  参考
 
1. Spring Security 官方文档：https://docs.spring.io/spring-security/site/docs/5.1.1.RELEASE/reference/htmlsingle/
2. JWT 官网：https://jwt.io/
3. JJWT开源工具参考：https://github.com/jwtk/jjwt#quickstart
4. 授权部分参考官方文档：https://docs.spring.io/spring-security/site/docs/5.1.1.RELEASE/reference/htmlsingle/#authorization

4. 动态授权部分，参考博客：https://blog.csdn.net/larger5/article/details/81063438

 
    
SecurityContext：安全上下文信息，用来存储用的认证信息
	实现：

SecurityContextRepository：安全上下文存储仓库，用来存储用户的安全上下文对象SecurityContext
	HttpSessionSecurityContextRespotiory:安全上下文对象存储到HttpSession对象中
	


