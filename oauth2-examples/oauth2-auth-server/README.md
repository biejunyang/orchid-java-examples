# oauth2-auth-server
> 此 demo 主要演示了 Spring Security OAuth2.0中认证服务器的实现。支持四种授权模式：授权码模式、客户端模式、密码模式、隐士模式。


## 1、JWT介绍
### JWT(JSON WEB TOKEN)
JWT是一种基于JSON数据传出的WEB开发标准。可以用做用户登录鉴权，存储用户的会话信息。

### 组成
JWT由三段文本组成：头部、负载、数字签名。三段文本通过.号拼接，如：
````
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ
````

头部(Header)：jwt的元数据，包含了jwt的类型和数字签名的加密算法
负载(Payload)：实际需要传输的数据。JWT规范中定义标准的字段声明，如：iss、sub、aud等。
数字签名(Signature)：确保Token的完整性，及识别Token的签发方。


### 3、优缺点：
优点：

1、会话信息不需要保存在服务端，非常适合分布式微服务

2、字包含：Token中包含了用户的相关信息，不需要在去数据库获取

3、简介快速、传输方便，可以添加到请求头，或参数中发送

缺点：

1、数据安全问题：jwt负载中的数据是经过Base64编码后的文本，是可以逆向解码的。所以token中不能存在民反信息

2、JWT令牌一旦签署，会一直有效直到Token的有效期。因为服务并没有存在会话状态信息。

3、数量问题，如果JWT中负载数据过多，会导致JWT的长度过长



## 2、pom依赖添加
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

## 3、Spring Security 权限控制原理

### 1、SecurityContextPersistenceFilter(安全上下文持久化过滤器):
当前请求的安全上的准备和获取。请求开始时从配置好的 SecurityContextRepository中获取 SecurityContext，然后把它设置给 SecurityContextHolder。在请求完成后将 SecurityContextHolder 持有的 SecurityContext 再保存到配置好的 SecurityContextRepository，同时清除 securityContextHolder 所持有的 SecurityContext；
SecurityContext中存储了用户的认证信息，SecurityContextRepository默认的实现为HttpSessionSecurityContextRespotiory，及默认从session对象获取认证信息。


### 2、FilterSecurityInterceptor(安全认证过滤器)：
Spring Security过滤器链中的最后一个过滤器，其作用是保护请求的资源，校验当前请求是否已经过认证，并且用户是否有权限访问该请求。未经过认证则抛出AuthenticationException异常，权限不够则抛出AccessDenyException异常。其实现流程：
````
通过SecurityContextHolder.getContext().getAuthentication()获取当请求上线文中的Authentication对象。通过该Authentication对象中的认证状态，和用户的角色，权限信息判断是否有权限访资源。
````


 
## 4、Spring Security JWT 实现原理
````
1、JWT鉴权中用户的认证信息Authentication对象不是存在在Session对象中，而是使用JWT Token来承载，发送给客户端。后端并不需要存储用户状态，客户端访问全资源时通过携带授权的JWT Token来访问资源。

2、通过实现JWT的鉴权过滤器，校验请求访问时携带的Token是否有效，有效则表示认证通过，然后解析Token获取token对应的用户认证信息，并且保存在SecurityContext中。<br/>
   注意token校验通过后，需要将token对应的认证信息保存到SecurityContext中，因为Spring Security最后的FilterSecurityInterceptor需要从中获取认证Authentication对象进行进一步权限认证。

3、实现登录请求端点，返回JWT Token.
````


## 5、Jwt鉴权实现

### Jwt权限认证过滤器JwtAuthorizationFilter
拦截所有需要认证的请求，校验用户请求中携带的Token,Token校验成功，则解析Token获取对应的用户认证信息Authentication对象，并放到安全上下文中，校验失败则返回错误码
```java
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
                        ResponseUtil.renderJson(response, R.error(e.getMessage()));
                        return;
                    }
                } else {
                    ResponseUtil.renderJson(response, R.error("invalid token", null));
                }
            } else {
                ResponseUtil.renderJson(response, R.error("未登录"));
            }
        }
    }

}

```

````
注意Token校验成功后,要生成用户已认证的Authentication信息,放入到SecurityContext中。
Spring Security最后面的安全拦截器FilterSecurityInterceptor会从中获取认证对象进行权限控制、
````

### Spring Security过滤器链中添加该过滤器
自定义的安全保护过滤器要添加在FilterSecurityInterceptor过滤器之前。

JWT并没有在后台存储用户登录状态，而是存储在Token传输给客户端。所以可以关闭Session功能。
```java
    /**
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/login").permitAll()
                .anyRequest().authenticated().and()
            .formLogin().disable()
            .sessionManagement().disable()
            .csrf().disable()
            .exceptionHandling()
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
                        ResponseUtil.renderJson(httpServletResponse, R.error("权限不够"));
                    }
                });

        JWTAuthorizationFilter jwtAuthorizationFilter=new JWTAuthorizationFilter();
        http.addFilterAt(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
    }
    
```

### 登录接口
用户名密码认证，认证成功返回生成的Token信息
```java
    @PostMapping("/login")
    public R login(@RequestBody Map<String, Object> userVo) throws JOSEException {
        if(ObjectUtil.isEmpty(userVo.get("username"))){
            return R.error("用户名不能为空!", null);
        }
        if(ObjectUtil.isEmpty(userVo.get("password"))){
            return R.error("密码不能为空!", null);
        }
        UserDetails userDetails=userDetailsService.loadUserByUsername(userVo.get("username").toString());
        if(userDetails==null){
            return R.error("用户名不能存在", null);
        }

        if(!passwordEncoder.matches(userVo.get("password").toString(), userDetails.getPassword())){
            return R.error("密码错误", null);
        }
        return R.success(JwtTokenUtil.createToken(userDetails.getUsername()));
    }
```

````
Spring Security默认的表单登录过程中使用的是UsernamePasswordAuthenticationFilter认证过滤器来完成登录认证的。
JWT认证中也可以实现一个认证过滤器处理登录认证。
````



## 6、参考
JSOE框架：https://blog.csdn.net/peterwanghao/article/details/98534636

nimbus jwt框架：https://connect2id.com/products/nimbus-jose-jwt/examples

JWT说明：http://blog.leapoahead.com/2015/09/06/understanding-jwt/

JWT官网：https://jwt.io/

