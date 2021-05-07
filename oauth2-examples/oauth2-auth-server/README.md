# oauth2-auth-server
> 此 demo 主要演示了 Spring Security OAuth2.0中Authorization Server认证服务器的实现。支持四种授权模式：授权码模式、客户端模式、密码模式、隐士模式。

## 1、Authorization Server介绍
用来处理用户认证和授权的服务提供者。访问受保护的用户服务时，必须要经过认证服务器的授权。

认证服务器需要实现的功能：
````
1、用户认证：用户授权之前，需要登录认证之后才能授权
2、授权：是否授权第三方应用响应操作权限
3、颁发令牌：授权通过后，生成令牌返回给客户端
4、校验令牌：客户端访问受保护资源时
5、刷新令牌：令牌到期后会自动失效，继续使用则需要刷新令牌
````


## 2、pom.xml依赖
```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.security.oauth.boot</groupId>
        <artifactId>spring-security-oauth2-autoconfigure</artifactId>
    </dependency>
```
````
spring-security-oauth2-autoconfigure依赖是spring security oauth2对spring boot应用的集成；用于自动配置oauth2。
但是该依赖并不在springboot的依赖管理（spring-boot-dependencies）中；而是在spring cloud的依赖管理(spring-cloud-dependencies)中。
````

## 3、Authorization Server本身Web安全配置
授权码模式中需要用户登录认证后，给第三方应用授权，所以认证服务器本身需要实现用户安全认证。

```java
@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("admin").password("{noop}123456").authorities("ADMIN");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .mvcMatchers("/.well-known/jwks.json").permitAll()
                .anyRequest().authenticated().and()
            .formLogin().permitAll().and()
            .logout().and()
            .csrf().ignoringRequestMatchers(request -> "/introspect".equals(request.getRequestURI()));
    }


    @Override
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
```


## 4、Authorization Server授权服务器配置

### 4.1、@EnableAuthorizationServer注解
使用注解@EnableAuthorizationServer，开始OAuth2认证服务器自动配置。自动导入了两个配置：

1、AuthorizationServerEndpointsConfiguration：OAuth2.0认证服务相关端点服务配置类。开启认证相关的端点服务，如：
````
   /oauth/authorize：授权端口，获取授权码，需要资源所有者用户登录授权
   /oauth/token：获取token，不用登录，只需要通过授权码换取token
   /oauth/confirm_access：用户确认授权的端点
   /oauth/error：认证失败
   /oauth/check_token：资源服务器用来校验token
   /oauth/token_key：如果jwt模式则可以用此来从认证服务器获取公钥
````

2、AuthorizationServerSecurityConfiguration：认证服务相关端点的安全约束配置类
````
定义授权端点默认的安全约束配置；该配置类是继承WebSecurityConfigurerAdapter；是spring security的配置类实现，并且顺序@Order为0。即该配置类的安全配置优先级较高。
````


### 4.2、自定义认证服务配置类
@EnableAuthorizationServer开启了OAuth2.0认证服务的默认配置，但是我们需要顶一个配置类继承AuthorizationServerConfigurerAdapter，进行自定的认证服务配置，如默认配置：

```java
@EnableAuthorizationServer
@Configuration
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {


    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        super.configure(security);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        super.configure(clients);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        super.configure(endpoints);
    }
}
```
````
实际上@EnableAuthorizationServer自动导入的配置类AuthorizationServerEndpointsConfiguration和AuthorizationServerSecurityConfiguration
会自动获取我们自定义的配置的AuthorizationServerConfigurer实现类，来重写默认配置。
````


### 4.3、ClientDetailsServiceConfigurer客户端认证配置
OAuth2.0中允许对第三方客户端进行授权。但是能够授权的第三方客户端需要经过认证。

```java
/**
     * 客户端设置
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//        1、内存中设置客户端信息inMemory()，注意授权码授权时，必须设置好重定向地址
        clients
            .inMemory().withClient("client").secret("{noop}client")
                .authorizedGrantTypes("client_credentials", "authorization_code", "refresh_token", "password", "implicit")
                .scopes("read","write")
                .redirectUris("http://baidu.com", "http://localhost:8083/client/login", "http://localhost:8083/client/hello","http://localhost:8084/client2/login")
                .accessTokenValiditySeconds(600_000_000).and();


        //2、从jdbc数据源中获取客户端信息，默认的表结构中
//        clients.jdbc(dataSource).passwordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder());
//        clients.withClientDetails(new JdbcClientDetailsService(dataSource));


//        3、自定义通过clientId获取客户端信息
//        clients.withClientDetails(new ClientDetailsService() {
//            @Override
//            public ClientDetails loadClientByClientId(String s) throws ClientRegistrationException {
//
//                return null;
//            }
//        });
    }
```
ClientDetailsServiceConfigurer：client客户端的信息配置，包括：clientId、secret、scope表示权限范围，可选项，用户授权页面时进行选择、authorizedGrantTypes授权方式、authorities授权权限。

客户端信息可以通过集中方式获取,设置ClientDetailsService实现对象：in-memory(内存中)，jdbc(提供数据源使用默认的表结构)，使用ClientDetailServic自定义读取客户信息，如：

````
1、InMemoryClientDetailsService：从内存中获取客户端信息
2、JdbcClientDetailsService：通过JDBC数据源，从数据库获取客户端信息，spring security oauth2实现中提供了默认的表结构
3、自定义ClientDetailsService实现类：自定义客户端加载实现
````

### 4.4、AuthorizationServerEndpointsConfigurer授权端点服务配置
授权服务端点信息配置，默认支持除了密码外的所有授权方式。可以对授权服务的相关参数进行配置，如：

````
1、TokenGranter对象：令牌的创建方式、实现中会根据授权方式的不同生成不同的Token。

2、TokenStore对象：令牌的存储方式，如：如InMemoryTokenStore内存中存储，JdbcTokenStore数据库存储，Jwt使用jwt方式存储

3、TokenEnhancer对象：令牌的增强处理，

4、AuthorizationServerTokenServices：令牌的管理对象，实现令牌的创建、获取、存储、增强处理。默认实现类是DefaultTokenService。
它将令牌的创建委托给TokenGranter对象实现、令牌的存储委托给TokenStore来实现、令牌的增强处理委托给TokenEhancer来实现。

5、AuthenticationManager：用户认证管理器,默认是不支持密码授权模式的。这是AuthorizationManager对象后则，支持密码授权模式。
因为密码模式需要使用用户认证管理器，来认证用户信息


````

### 4.5、AuthorizationServerSecurityConfigurer授权端点安全约束设置

授权服务端点默认的安全约束规则：
````
    /oauth/authorize：获取授权码端点，需要用户登录认证
    /oauth/token：获取令牌端点不要用户认证，并且如果配置支持allowFormAuthenticationForClients的，且url中有client_id和client_secret的会走客户端认证通过ClientCredentialsTokenEndpointFilter来保护；
    如果没有支持allowFormAuthenticationForClients或者有支持但是url中没有client_id和client_secret的，走Basic认证保护。
    /oauth/check_token:这个走basic认证保护
    /oauth/confirm_access:这个需要用户认证保护，否则报500
    /oauth/error:这个可以不用认证保护
````

常用配置：
````
security.tokenKeyAccess("permitAll()")：一般获取token的端点是不需要限制的，只需要通过授权码获取token即可
security.checkTokenAccess("isAuthenticated()")：检查token是否过期时，刷新令牌授权将包含对用户详细信息的检查，以确保该帐户仍然活动
allowFormAuthenticationForClients()：获取token端点：如果配置支持allowFormAut henticationForClients的，且url中有client_id和client_secret的会走
ClientCredentialsTokenEnd po intFilter来保护，如果没有支持allowFormAuthenticationForClients或者有支持但是url中没有client_id和client_secret的，走basic认证保护
````

```java
    /**
     * 2、授权服务端点安全约束
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .tokenKeyAccess("permitAll()")// jwt获取令牌key端点不要认证
                .checkTokenAccess("isAuthenticated()")//校验令牌端点需要授权过
                .allowFormAuthenticationForClients();
    }

```

### 4.6、认证服务器完整配置
```java

@EnableAuthorizationServer
@Configuration
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {


    @Autowired
    private AuthenticationManager authenticationManager;
    /**
     * 客户端设置
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

//        1、内存中设置客户端信息inMemory()，注意授权码授权时，必须设置好重定向地址
        clients
                .inMemory().withClient("client").secret("{noop}client")
                .authorizedGrantTypes("client_credentials", "authorization_code", "refresh_token", "password", "implicit")
                .scopes("read","write")
                .redirectUris("http://baidu.com", "http://localhost:8083/client/login", "http://localhost:8083/client/hello","http://localhost:8084/client2/login")
                .accessTokenValiditySeconds(600_000_000).and();


        //2、从jdbc数据源中获取客户端信息，默认的表结构中
//        clients.jdbc(dataSource).passwordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder());
//        clients.withClientDetails(new JdbcClientDetailsService(dataSource));


//        3、自定义通过clientId获取客户端信息
//        clients.withClientDetails(new ClientDetailsService() {
//            @Override
//            public ClientDetails loadClientByClientId(String s) throws ClientRegistrationException {
//
//                return null;
//            }
//        });
    }


    /**
     * 1、授权服务端点设置
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
//        super.configure(endpoints);
        endpoints.authenticationManager(authenticationManager);
//        endpoints.authenticationManager(authenticationManager) //指定用户认证管理器
//                .tokenStore(tokenStore)
//                .accessTokenConverter(accessTokenConverter);


    }


    /**
     * 2、授权服务端点安全约束
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .tokenKeyAccess("permitAll()")// jwt获取令牌key端点不要认证
                .checkTokenAccess("isAuthenticated()")//校验令牌端点需要授权过
                .allowFormAuthenticationForClients();
    }

}
```

以上认证服务器配置基本上实现了，OAuth2.0中的四种授权模式。默认使用DefaultTokenServices令牌管理对象生成一个随机数令牌、并且令牌存储在内存中。

令牌存储在内存在，在单服务应用上体现良好，但是不适合认证服务器集群部署，并且并不适用与高并发，并且服务重启后Token会清空。


## 5、OAuth2.0 授权模式


### 5.1、授权码模式(authorization_code)：
功能最完整、流程最严密的授权模式。它的特点就是通过客户端的后台服务器，与"服务提供商"的认证服务器进行互动；通过资源所有这用户登录，并且同意授权后，才能获取令牌。首先是获取授权码，必须经过用户登录，并授权；然后在通过授权码获取令牌（Token）。


获取授权码请求参数：
````
response_type：表示授权类型，必选项，此处的值固定为"code"
client_id：表示客户端的ID，必选项
client_secret：客户端的密码，可选
redirect_uri：表示重定向URI，可选项
scope：表示申请的权限范围，可选项
state：表示客户端的当前状态，指定任意值，服务器会原封不动地返回这个值。
````

获取授权码请求端点：
````
http://localhost:8080/oauth/authorize?response_type=code&client_id=client&redirect_uri=http://baidu.com
````


获取令牌请求端点：
````
POST请求：POST http://localhost:8080/oauth/token?grant_type=authorization_code&client_id=client&client_secret=client&code=klgv4L&redirect_uri=http://baidu.com
````


注意：
````
授权码认证中/oauth/token端点也需要开启所有要开启allowFormAuthenticationForC lients让()支持客户端认证方式；虽然获取授权码时已经进行basic用户认证了；
但是获取code后重定向到了我们自己的页面，在此调用token编码时，并不是在一个会话中则token显示未授权；所有也要开启allowFormAuthenticationForClients();
````


### 5.2、客户端模式(client_credentials)：
客户端直接通过客户端凭证获取令牌;不要用户登录认证，

请求参数：
````
response_type：表示授权类型，必选项，此处的值固定为"client_credentials"
client_id：表示客户端的ID，必选项
client_secret：客户端的密码，可选项
scope：表示申请的权限范围，可选项；若无则申请所有权限；若有则表示申请某一个权限（前提客户端配置中该客户端拥有该权限）
````

请求端点：
````
POST请求：http://localhost:8080/oauth/token?grant_type=client_credentials&client_id=client&client_secret=client&scope=write
````

注意：
````
1、客户端模式中，是不需要用户认证及授权；是通过客户端凭证来获取Token；获取Token的端点安全约束需要使其客户端认证，
所有要开启allowFormAuthenticationForClients让/oauth/token支持client_id以及client_secret作登录认证，而不走Basic认证。

2、客户端模式是客户端本身请求验证的行为，所以无需用户“认证”，不牵涉任何用户的行为。微服务体系不怎么使用这种模式，
因为这种模式可以轻易地通过refresh token来不断获取access token，并且也可以使用动态的注册流程，在集成服务时自动执行所有流
````

### 5.3、密码模式(password):
直接在客户端使用用户的账户密码来获取令牌，因为是直接在客户端输入用户名和密码，并不安全，所以一般只是在内部客户端系统中使用。

请求参数：
````
response_type：表示授权类型，必选项，此处的值固定为"password"
username:用户名
password:用户密码
client_id：表示客户端的ID，必选项
client_secret：客户端的密码，可选项
scope：表示申请的权限范围，可选项；若无则申请所有权限；若有则表示申请某一个权限（前提客户端配置中该客户端拥有该权限）
````

请求端点：
````
POST请求：http://localhost:8080/oauth/token?grant_type=password&username=admin&password=123456&client_id=client&client_secret=client
````

注意：
````
密码模式需要使用用户认证来认证用户名和密码，所有授权服务端点设置中需指定用户认证管理器AuthenticationManager或者UserDetailSerice获取认证用户信息。
````


### 5.4、隐式模式(implict):

授权码模式的简化版本，不需要先获取授权码，用户登录授权后直接发送令牌；重定向指定地址。当使用隐式模式时，第三方应用始终需要通过重定向URI来注册，这样能确保不会将access token传给不需要验证的客户端。

请求参数：
````
response_type：表示授权类型，必选项，此处的值固定为"token",表示直接获取Token
redirect_uri：回调url 一定要与授权服务器配置保持一致，否则得不到授权码
client_id：表示客户端的ID，必选项
scope：表示申请的权限范围，可选项；若无则申请所有权限；若有则表示申请某一个权限（前提客户端配置中该客户端拥有该权限）
state：表示客户端的当前状态，指定任意值，服务器会原封不动地返回这个值。

````

请求端点：
````
GET请求：http://localhost:8080/oauth/authorize?client_id=client&redirect_uri=http://baidu.com&response_type=token&state=xyz
````


### 5.5、刷新令牌(refresh_token):

请求参数：
````
grant_type：表示授权类型，必选项，此处的值固定为"refresh_token",表示刷新令牌
refresh_token：需要刷新的令牌
client_id：表示客户端的ID，必选项
client_secret：客户端的密码，必选项

````

请求端点：
````
POST请求：http://localhost:8080/oauth/token?grant_type=refresh_token&client_id=client&client_secret=client&refresh_token=xcxcxxcxcxx
````


## 6、TokenStore令牌持久化管理
认证服务器的主要作用是颁发令牌，和校验令牌，所以生成令牌后需要存储起来，便于后面进行令牌的校验。Spring Security OAuth2实现中默认使用InMemoryTokenStore将令牌存储在内存中。

但是并不是使用集群部署的应用，以及高并发的情况，除此外还有几种TokenStore实现：

````
InMemoryTokenStore：这个是OAuth2默认采用的实现方式。在单服务上可以体现出很好特效（即并发量不大，并且它在失败的时候不会进行备份），大多项目都可以采用此方法。
毕竟存在内存，而不是磁盘中，调试简易。使用ConcurrentHashMap存储Token数据信息，线程安全支持多并发。


JdbcTokenStore：这个是基于JDBC的实现，令牌（Access Token）会保存到数据库。这个方式，可以在多个服务之间实现令牌共享。


JwtTokenStore：jwt全称 JSON Web Token。这个实现方式不用管如何进行存储（内存或磁盘），因为它可以把相关信息数据编码存放在令牌里。JwtTokenStore 不会保存任何数据，
但是它在转换令牌值以及授权信息方面与 DefaultTokenServices 所扮演的角色是一样的。但有两个缺点： 
        撤销一个已经授权的令牌会很困难，因此只适用于处理一个生命周期较短的以及撤销刷新令牌。
        令牌占用空间大，如果加入太多用户凭证信息，会存在传输冗余
    
RedisTokenStore：令牌存储Redis缓存平台。
````


### 6.1、RedisTokenStore(Redis令牌管理)
使用Redis来存储令牌、Reids的高性能、高并发处理特点，用来存储令牌既适合分布式应用，又能处理高并发的情况。
```java
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
            .authenticationManager(authenticationManager)
            .tokenStore(tokenStore());
    }
    
    @Bean
    public TokenStore tokenStore(){
        return new RedisTokenStore(redisConnectionFactory);
    }

```

### 6.2、JwtTokenStore(JWT令牌管理)
使用JWT来存储令牌时，实际并不会在认证服务器后台内存或本地持久化生成的令牌。而是直接把认证用户的相关信息已json串的形式编码到JWT令牌中，然后返回给客户端。

资源服务器校验上只需要在本地校验JWT令牌的有效性，不需要想认证服务器请求校验令牌。JWT令牌中已然包括了用户认证的相关信息。

JWT将用户认证信息直接保存在Token中并不安全，所以Spring OAuth2提供了JwtAccessTokenConverter来怼令牌进行编码和解码。

认证服务器生成JWT令牌，使用秘钥进行数字签名、资源服务器使用相同对应的秘钥进行签名验证，验证成功则令牌和有效的，并从中可以解析出认证用信息。

JWT数字签名的作用：
````
签名方身份识别：只要使用认证服务器提供的秘钥进行令牌解密、则令牌一定是认证服务器颁发的
Token的完整性校验：保证Token中的用户信息，没有被串改
````



JwtAccessTokenConverter对象实现令牌的编码和解码。Spring Security OAuth2实现中默认支持两种加密算法：

对称加密算法(HMAC加密算法)：JWT加密可解密使用使用相同秘钥如：
```java
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();

        //对称加密签名
        converter.setSigningKey("123456");

        DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
        accessTokenConverter.setUserTokenConverter(new SubjectAttributeUserTokenConverter());
        converter.setAccessTokenConverter(accessTokenConverter);

        return converter;
    }

```


非对称加密算法，默认是RSA非对称加密算法，使用私钥加密，解析时使用公钥解密，如：

```java
    /**
     * Access Token Converter Bean
     * @param keyPair
     * @return
     */
    @Bean
    public JwtAccessTokenConverter accessTokenConverter(KeyPair keyPair) {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();

//      非对称加密算法
        converter.setKeyPair(keyPair);

        DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
        accessTokenConverter.setUserTokenConverter(new SubjectAttributeUserTokenConverter());
        converter.setAccessTokenConverter(accessTokenConverter);

        return converter;
    }



    /**
     * JWT Token Signed Secet
     * An Authorization Server will more typically have a key rotation strategy, and the keys will not
     * be hard-coded into the application code.
     *
     * For simplicity, though, this sample doesn't demonstrate key rotation.
     */
    @Bean
    public KeyPair keyPair() {
        try {
            String privateExponent = "3851612021791312596791631935569878540203393691253311342052463788814433805390794604753109719790052408607029530149004451377846406736413270923596916756321977922303381344613407820854322190592787335193581632323728135479679928871596911841005827348430783250026013354350760878678723915119966019947072651782000702927096735228356171563532131162414366310012554312756036441054404004920678199077822575051043273088621405687950081861819700809912238863867947415641838115425624808671834312114785499017269379478439158796130804789241476050832773822038351367878951389438751088021113551495469440016698505614123035099067172660197922333993";
            String modulus = "18044398961479537755088511127417480155072543594514852056908450877656126120801808993616738273349107491806340290040410660515399239279742407357192875363433659810851147557504389760192273458065587503508596714389889971758652047927503525007076910925306186421971180013159326306810174367375596043267660331677530921991343349336096643043840224352451615452251387611820750171352353189973315443889352557807329336576421211370350554195530374360110583327093711721857129170040527236951522127488980970085401773781530555922385755722534685479501240842392531455355164896023070459024737908929308707435474197069199421373363801477026083786683";
            String exponent = "65537";

            RSAPublicKeySpec publicSpec = new RSAPublicKeySpec(new BigInteger(modulus), new BigInteger(exponent));
            RSAPrivateKeySpec privateSpec = new RSAPrivateKeySpec(new BigInteger(modulus), new BigInteger(privateExponent));
            KeyFactory factory = KeyFactory.getInstance("RSA");
            return new KeyPair(factory.generatePublic(publicSpec), factory.generatePrivate(privateSpec));
        } catch ( Exception e ) {
            throw new IllegalArgumentException(e);
        }
    }

```



DefaultUserAuthenticationConverter对象：用来是自定义JWT令牌中包含的用户信息，如：
```java
class SubjectAttributeUserTokenConverter extends DefaultUserAuthenticationConverter {
    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("sub", authentication.getName());
        if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            response.put(AUTHORITIES, AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
        }
        return response;
    }
}
```


构建并设置JwtTokenStore,

```java

    @Bean
    public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
        return new JwtTokenStore(jwtAccessTokenConverter);
    }
    
    
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

        endpoints.authenticationManager(authenticationManager) //指定用户认证管理器
                .tokenStore(tokenStore)
                .accessTokenConverter(accessTokenConverter)
        ;
    }
    
```


### 总之：

令牌存储在内存中是使用方便，但应用中使用良好，不适合高并发，集群部署内；并且不能持久化，当服务重启时，全部token失效

令牌存储在数据库中时，效率太低，令牌校验时需要访问数据库，高并发下数据库的压力太大

令牌存储在JWT中时，效率高，后台不需要存储Token，非常适合分布式服务，但是令牌无法撤销

令牌存储在Redis中是，适合分布式应该，并且redis的高并发，高性能特点，适合高并发情况




## 7、认证服务器对外的端点服务
    
/oauth/authorize：获取授权码端点，需要用户登录认证

/oauth/token：获取令牌端点

/oauth/check_token:令牌校验端点

其他可以实现服务端点：

/userInfo：获取token对象用户信息
```java
    @Autowired
    private TokenStore tokenStore;


    /**
     * 获取Token对应的信息
     * @param token
     * @return
     */
    @GetMapping("/userInfo")
    @ResponseBody
    public Object userInfo(String token){
        if(ObjectUtil.isNotEmpty(token)){
            return tokenStore.readAuthentication(token);
        }
        return null;
    }
```

/.well-known/jwks.json：JWT令牌实现中使用非对称加密算法时，用来公布公钥信息
```java
@FrameworkEndpoint
class JwkSetEndpoint {
    KeyPair keyPair;

    JwkSetEndpoint(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    @GetMapping("/.well-known/jwks.json")
    @ResponseBody
    public Map<String, Object> getKey() {
        RSAPublicKey publicKey = (RSAPublicKey) this.keyPair.getPublic();
        RSAKey key = new RSAKey.Builder(publicKey) .keyUse(KeyUse.SIGNATURE)
                .keyID(UUID.randomUUID().toString()).build();
        return new JWKSet(key).toJSONObject();
    }
}
```




## 8、客户端认证
OAuth2授权是对第三方应用客户端授予权限访问受认证服务器保护的资源，所以授权时首先需要对申请token的客户端进行认证（校验client_id和client_secret的正确性）。

Spring Cloud OAuth2默认使用Basic认证来校验客户端；使用BasicAuthenticationFilter过滤器来校验client_id和client_secret。

同时支持使用表单认证的方式来认证客户端，client_id和client_secret当做请求参数提交到后台进行校验。实现过滤器是ClientCredentialsTokenEndpointFilter。

开启客户端表单认证：
```
@Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.allowFormAuthenticationForClients()
        ;
    }
```
开启客户表单认证后，并且当请求URL中含有client_id和client_secret参数时才会走客户点表单认证（ClientCredentialsTokenEndpointFilter）；否则任然走客户端Basic认证

自定义客户端认证：使用自定义认证过滤器或者内置的Basic认证和ClientCredentialsTokenEndpointFilter，如：
````
security.addTokenEndpointAuthenticationFilter(new ClientCredentialsTokenEndpointFilter());
````



## 9、认证授权流程（密码模式为例）

1、客户端认证：Basic认证(认证)或者客户端表单认证(ClientCredentialsTokenEndpointFilter)

2、进入/oauth/token端点（TokenEndpoint）获取token

3、请求参数校验如grant_type授权模式；scope权限参数

4、用户认证，用户名，密码，手否锁定，过期等校验

5、全部校验成功，则返回token信息，校验失败则进行授权失败处理流程




## 10、认证失败异常处理

Spring cloud OAuth2认证失败常见异常：
````
1、客户端认证失败异常，默认返回401状态码和数据{"error":"invalid_client","error_description":"Bad client credentials"}
2、用户认证失败异常，如用户名或密码错误返回401状态码，请求参数错误返回400状态码，请求方式(支持POST)错误返回405状态码
````

注意：Spring Security认证过程中认证失败抛出异常时，默认都对异常进行了捕获和处理，我**们无法使用全局异常处理@RestControllerAdvice来捕获异常来进行处理**。

如客户端认证异常发生在客户端认证过滤器中如BasicAuthenticationFilter或ClientCredentialsTokenEndpointFilter中；认证发生异常后直接异常处理后返回。都还没有进入到DispatcherServlet请求处理流程(Controller)；Spring Boot的统一异常处理是在DispatchServlet中捕获后统一处理，所以这里获取不到。

用户认证失败异常是发生在认证端点如TokenEndpoint中，正常可以在@ControllerAdvice中统一异常处理，但是Spring Cloud认证端点中对相关的异常已经进行了捕获和处理，所以会在进入到ControllerAdvice异常处理中，如：

TokenEndpint异常处理,使用异常处理器来处理异常ExceptionTranslator
```java
@FrameworkEndpoint
public class TokenEndpoint extends AbstractEndpoint {
    private OAuth2RequestValidator oAuth2RequestValidator = new DefaultOAuth2RequestValidator();
    private Set<HttpMethod> allowedRequestMethods;

    public TokenEndpoint() {
        this.allowedRequestMethods = new HashSet(Arrays.asList(HttpMethod.POST));
    }

    @RequestMapping(
        value = {"/oauth/token"},
        method = {RequestMethod.GET}
    )
    public ResponseEntity<OAuth2AccessToken> getAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        if (!this.allowedRequestMethods.contains(HttpMethod.GET)) {
            throw new HttpRequestMethodNotSupportedException("GET");
        } else {
            return this.postAccessToken(principal, parameters);
        }
    }

    @RequestMapping(
        value = {"/oauth/token"},
        method = {RequestMethod.POST}
    )
    public ResponseEntity<OAuth2AccessToken> postAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        if (!(principal instanceof Authentication)) {
            throw new InsufficientAuthenticationException("There is no client authentication. Try adding an appropriate authentication filter.");
        } else {
            String clientId = this.getClientId(principal);
            ClientDetails authenticatedClient = this.getClientDetailsService().loadClientByClientId(clientId);
            TokenRequest tokenRequest = this.getOAuth2RequestFactory().createTokenRequest(parameters, authenticatedClient);
            if (clientId != null && !clientId.equals("") && !clientId.equals(tokenRequest.getClientId())) {
                throw new InvalidClientException("Given client ID does not match authenticated client");
            } else {
                if (authenticatedClient != null) {
                    this.oAuth2RequestValidator.validateScope(tokenRequest, authenticatedClient);
                }

                if (!StringUtils.hasText(tokenRequest.getGrantType())) {
                    throw new InvalidRequestException("Missing grant type");
                } else if (tokenRequest.getGrantType().equals("implicit")) {
                    throw new InvalidGrantException("Implicit grant type not supported from token endpoint");
                } else {
                    if (this.isAuthCodeRequest(parameters) && !tokenRequest.getScope().isEmpty()) {
                        this.logger.debug("Clearing scope of incoming token request");
                        tokenRequest.setScope(Collections.emptySet());
                    }

                    if (this.isRefreshTokenRequest(parameters)) {
                        tokenRequest.setScope(OAuth2Utils.parseParameterList((String)parameters.get("scope")));
                    }

                    OAuth2AccessToken token = this.getTokenGranter().grant(tokenRequest.getGrantType(), tokenRequest);
                    if (token == null) {
                        throw new UnsupportedGrantTypeException("Unsupported grant type: " + tokenRequest.getGrantType());
                    } else {
                        return this.getResponse(token);
                    }
                }
            }
        }
    }

   
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<OAuth2Exception> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) throws Exception {
        if (this.logger.isInfoEnabled()) {
            this.logger.info("Handling error: " + e.getClass().getSimpleName() + ", " + e.getMessage());
        }

        return this.getExceptionTranslator().translate(e);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<OAuth2Exception> handleException(Exception e) throws Exception {
        if (this.logger.isErrorEnabled()) {
            this.logger.error("Handling error: " + e.getClass().getSimpleName() + ", " + e.getMessage(), e);
        }

        return this.getExceptionTranslator().translate(e);
    }

    @ExceptionHandler({ClientRegistrationException.class})
    public ResponseEntity<OAuth2Exception> handleClientRegistrationException(Exception e) throws Exception {
        if (this.logger.isWarnEnabled()) {
            this.logger.warn("Handling error: " + e.getClass().getSimpleName() + ", " + e.getMessage());
        }

        return this.getExceptionTranslator().translate(new BadClientCredentialsException());
    }

    @ExceptionHandler({OAuth2Exception.class})
    public ResponseEntity<OAuth2Exception> handleException(OAuth2Exception e) throws Exception {
        if (this.logger.isWarnEnabled()) {
            this.logger.warn("Handling error: " + e.getClass().getSimpleName() + ", " + e.getMessage());
        }

        return this.getExceptionTranslator().translate(e);
    }

    //.............
}

```



### 10.1、自定义客户端认证失败处理

客户端认证处理在过滤器BasicAuthenticationFilter或者ClientCredentialsTokenEndpointFilter中实现，认证失败后，过滤器会将认证异常交个AuthenticationEntryPoint来处理（**Spring Security认证处理流程**），如：

表单认证异常处理：认证失败调用AuthenticationFailureHandler认证失败处理器处理异常，该认证失败处理器调用AuthenticationEntryPoint对象来处理异常，默认实现对象是AuthenticationEntryPoint。
```
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        this.setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                if (exception instanceof BadCredentialsException) {
                    exception = new BadCredentialsException(((AuthenticationException)exception).getMessage(), new BadClientCredentialsException());
                }

                ClientCredentialsTokenEndpointFilter.this.authenticationEntryPoint.commence(request, response, (AuthenticationException)exception);
            }
        });
        this.setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            }
        });
    }
```

Basic认证异常处理，同样也是会调用AuthenticationEntryPoint对象来处理异常，认证失败后，会默认跳转到登录页面



自定义认证失败处理：
1、不能使用默认的ClientCredentialsTokenEndpointFilter过滤器对象，所以不开启默认客户端表单认证，**去掉该代码**：
```
//security.allowFormAuthenticationForClients();
```

2、设置自定义认证过滤器对象，继承ClientCredentialsTokenEndpointFilter实现对象，且配置自定义的认证失败处理器，如:

自定义客户端认证处理
```
public class CustomClientCredentialsTokenEndpointFilter extends ClientCredentialsTokenEndpointFilter {

    private AuthorizationServerSecurityConfigurer configurer;
    
    private AuthenticationEntryPoint authenticationEntryPoint;

    public CustomClientCredentialsTokenEndpointFilter(AuthorizationServerSecurityConfigurer configurer) {
        this.configurer = configurer;
    }

    @Override
    public void setAuthenticationEntryPoint(AuthenticationEntryPoint authenticationEntryPoint) {
        // 把父类的干掉
        super.setAuthenticationEntryPoint(null);
        this.authenticationEntryPoint = authenticationEntryPoint;
    }


    //认证过滤器中，必须要注入人认证管理器对象
    @Override
    protected AuthenticationManager getAuthenticationManager() {
        return configurer.and().getSharedObject(AuthenticationManager.class);
    }

    //设置认证失败，和认证成功处理器，认证失败调用AuthenticationEntryPoint对象进行失败处理
    @Override
    public void afterPropertiesSet() {
        setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                authenticationEntryPoint.commence(httpServletRequest, httpServletResponse, e);
            }

        });
        setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
                // 无操作-仅允许过滤器链继续到令牌端点
            }
        });
    }
}
```
添加自定义认证处理器，如：
```java
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        AuthenticationEntryPoint authenticationEntryPoint=new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                e.printStackTrace();
                httpServletResponse.getWriter().print("failed");
            }
        };
        CustomClientCredentialsTokenEndpointFilter endpointFilter = new CustomClientCredentialsTokenEndpointFilter(security);
        endpointFilter.afterPropertiesSet();
        endpointFilter.setAuthenticationEntryPoint(authenticationEntryPoint);
        security.addTokenEndpointAuthenticationFilter(endpointFilter);
        
        security
            // jwt获取令牌key端点不要认证
            .tokenKeyAccess("permitAll()")
            //校验令牌端点需要授权过
            .checkTokenAccess("isAuthenticated()")
//            .allowFormAuthenticationForClients()
        ;

    }
```

Basic认证同理


### 10.2、自定义用户认证失败处理

用户认证失败异常默认是在端点对象本地@ExceptionHandler进行异常处理的，本地异常处理时回调用异常处理器ExceptionTranslator对象来处理不同类型的异常，我们只需要自定义异常处理器对象即可，如：


````
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        //指定用户认证管理器
        endpoints.authenticationManager(authenticationManager)
	            .tokenStore(tokenStore)
                .accessTokenConverter(accessTokenConverter)
                //认证失败
                .exceptionTranslator(new WebResponseExceptionTranslator<OAuth2Exception>() {
                    @Override
                    public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
                        e.printStackTrace();
                        return null;
                    }
                })
        ;
    }
````


## 11、WebSecurityConfig和ResourceServerConfig安全配置都存在时注意：
a、@EnableResourceServer默认会保护所有请求，内置端点除外(@FrameworkEndpoint注解定义)，也可以自定义指定保护的请求资源，如：
```
    @Override
    public void configure(HttpSecurity http) throws Exception {
//        super.configure(http);
        http.antMatcher("/user/**").authorizeRequests().anyRequest().authenticated();
    }
```

b、WebSecurityConfig安全约束配置，对所有请求都有效，包含内置端点请求。**_并且配置类优先级较高，所有会被ResourceServerConfig的配置覆盖_**。



## 12、参考
https://www.cnblogs.com/haoxianrui/p/14028366.html

