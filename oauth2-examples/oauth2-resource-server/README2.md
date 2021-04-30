# Spring Security Oauth2中resource server实现支持

资源服务器的作用就是保护资源，验证令牌是否有效，有效则允许访问受保护的资源。针对认证服务器不同类型的Token存储方式时实现方式不同。

Spring Cloud OAuth2 实现中主要通过ResourceServerTokenServices的接口实现类对象来实现token的校验以及token对应数据的解析。

及通过token解析出对应的OAuth2AccessToken和OAuth2Authentication对象，如接口定义：
```java

public interface ResourceServerTokenServices {
    //通过token获取到OAuth2Authentication对象（包含用户认证信息）
    OAuth2Authentication loadAuthentication(String var1) throws AuthenticationException, InvalidTokenException;

    //通过token获取到OAuth2AccessToken对象
    OAuth2AccessToken readAccessToken(String var1);
}

```

ResourceServerTokenServices实现类：
```
1、RemoteTokenServices：远程调用认证服务器的token校验接口，并获取Token数据信息后，解析成OAuth2Authentication和OAuth2AccessToken对象。

2、UserInfoTokenServices：和RemoteTokenServices类似，实际也是远程调用获取用户信息的端点，获取token对应用户数据，并解析成OAuth2Authentication象。
区别是不支持获取OAuth2AccessToken对象信息

3、DefaultTokenServices：本地校验token，和获取token数据信息。获取token数据时回调用TokenStore对象来获取token数据，主要有几种方式：
    a、InmemoryTokenStore：从本地内存中后获取Token数据
    b、RedisTokenStore：从redis缓存中后去Token数据
    c、JwtTokenStore：本地解析jwt token获取数据

```



## 1、RemoteTokenServices远程Token校验和解析

参数：设置获取Token信息的远程端点信息
```
security:
  oauth2:
    client:
      client-id: client
      client-secret: client
    resource:
      token-info-uri: http://localhost:9001/oauth/check_token
```

配置：定义RemoteTokenServices对象，并设置将远程获取的Token数据解析成OAuth2Authentication和OAuth2AccessToken对象的
数据转换器DefaultAccessTokenConverter（可以使用默认配置），如：

```java
/**
 * 配置security.oauth2.resource.token-info-uri参数,进行远程Token校验
 */
@Configuration
@Conditional({ResourceServerTokenCondition.TokenInfoCondition.class})
static class TokenInfoServicesConfiguration{


    private final ResourceServerProperties resource;

    private TokenInfoServicesConfiguration(ResourceServerProperties resource) {
        this.resource = resource;
    }

    /**
     * Token 存储在认证服务端内存中,需要进行远程token校验
     * @return
     */

    @Bean
    @Primary
    public RemoteTokenServices remoteTokenServices(AccessTokenConverter accessTokenConverter) {
        RemoteTokenServices services = new RemoteTokenServices();
        services.setCheckTokenEndpointUrl(resource.getTokenInfoUri());
        services.setClientId(this.resource.getClientId());
        services.setClientSecret(this.resource.getClientSecret());
        services.setAccessTokenConverter(accessTokenConverter);
        return services;
    }
    

    /**
     * 将Token数据（Map）转换成OAuth2AccessToken和OAuth2Authentication对象的转换器
     * @param userAuthenticationConverter
     * @return
     */
    @Bean
    public DefaultAccessTokenConverter defaultAccessTokenConverter(UserAuthenticationConverter userAuthenticationConverter){
        DefaultAccessTokenConverter accessTokenConverter=new DefaultAccessTokenConverter();
        accessTokenConverter.setUserTokenConverter(userAuthenticationConverter);
        return accessTokenConverter;
    }

    /**
     * 将Token数据（Map）转换成OAuth2Authentication对象的转换器
     * @return
     */
    @Bean
    public UserAuthenticationConverter userAuthenticationConverter(){
        return new SubjectAttributeUserTokenConverter();
    }
}
```

注意：
````
无论认证服务器使用何种方式（inmemory，redis，jwt）存储token时，只要定义获取token数据的远程接口；资源服务器都可以使用此种方式来校验及获取token数据。

spring cloud oauth2认证服务器实现中内置了默认的校验端点/oauth/check_token；也可以手动实现认证接口；token校验失败则token无效，校验成功则返回token对应的数据。

但是此种方式每次token校验时，都会远程调用校验请求；增加了认证服务器的压力，并且当认证服务器宕机时，无法校验。
````



## 2、UserInfoTokenServices远程获取token对应的用户信息

参数：设置获取用户信息的远程端点信息
```
security:
  oauth2:
    client:
      client-id: client
      client-secret: client
    resource:
      user-info-uri: http://localhost:9001/userInfo
```

配置：定义UserInfoTokenServices对象，并设置将远程获取的用户数据解析成OAuth2Authentication的解析器(也可以使用默认的)，包括：

AuthoritiesExtractor：权限数据解析器，从获取到的用户数据解析出权限信息
PrincipalExtractor：用户数据解析器，将获取到的数据解析成用户对象

**ResourceServerTokenServicesConfiguration默认配置中已经配置了UserInfoTokenServices对象**

```java
/**
 * 配置security.oauth2.resource.user-info-uri参数,进行远程Token校验
 * ResourceServerTokenServicesConfiguration默认配置中已经配置了UserInfoTokenServices对象
 * 从远程端点user-info-uri中获取认证信息，并调用AuthoritiesExtractor和PrincipalExtractor对象从
 * 返回的结果信息中，解析出用户认证信息，注入这两个对象实现自定义Token数据解析
 */
@Configuration
@Conditional({ResourceServerTokenCondition.UserInfoCondition.class})
static class UserInfoServicesConfiguration{


    private final ResourceServerProperties resource;

    private UserInfoServicesConfiguration(ResourceServerProperties resource) {
        this.resource = resource;
    }


    /**
     * 根据获取到的Token对应的数据信息，解析出权限数据
     * 认证服务器返回数据不同，解析方式不同
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthoritiesExtractor authoritiesExtractor(){
//        return new FixedAuthoritiesExtractor();
        return map -> {
            Object authorities = map.get("authorities");

            List<GrantedAuthority> grantedAuthorities=null;

            if(authorities!=null && authorities instanceof List){
                grantedAuthorities= AuthorityUtils.createAuthorityList(((List<String>)authorities).toArray(new String[]{}));
            }

            return grantedAuthorities;
        };
    }


    /**
     * 根据获取到的Token对应的数据信息，解析出用户数据
     * 认证服务器返回数据不同，解析方式不同
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public PrincipalExtractor principalExtractor(){
        return map -> {
            AuthUser authUser=new AuthUser();
            authUser.setUsername(map.get("user_name").toString());
            Object authorities = map.get("authorities");

            List<GrantedAuthority> grantedAuthorities=null;

            if(authorities!=null && authorities instanceof List){
                grantedAuthorities= AuthorityUtils.createAuthorityList(((List<String>)authorities).toArray(new String[]{}));
                authUser.setAuthorities(grantedAuthorities);
            }
            Map<String, Object> addtionalInfomation=new HashMap<>(map);
            addtionalInfomation.remove("user_name");
            addtionalInfomation.remove("authorities");
            authUser.setAdditionalInformation(addtionalInfomation);
            return authUser;
        };
    }
}
```

注意：
````
无论认证服务器使用何种方式（inmemory，redis，jwt）存储token时，只要定义获取用户数据的远程接口；资源服务器都可以使用此种方式来校验及获取用户数据。

spring cloud oauth2认证服务器实现中没有内置了端点；需要也可以手动实现；token校验失败则token无效，校验成功则返回token对应的用户数据。

但是此种方式每次token校验时，都会远程调用校验请求；增加了认证服务器的压力，并且当认证服务器宕机时，无法校验。

和RemoteTokenServices不同的是，他不支持获取OAuth2AccessToken对象。
````



## 3、DefaultTokenServices本地token校验和解析
DefaultTokenServices对于在本地从内存，数据库，redis，jwt中获取Token对象的数据(获取不到，则token错误)。

获取token数据时，回调用TokenStore对象获取数据，解析成OAuth2AccessToken和OAuth2Authentication对象。

注意：
````
从内存中获取Token数据适用于认证服务和资源服务不分离，是同一个服务的情况。

从数据库中获取每次校验都要从数据库读取数据，增加数据库的压力并且性能低

所以本地解析一般适用于从Redis缓存和jwt中读取数据
````

### 3.1、RedisTokenStore：从redis中读取数据

参数：设置redis相关参数信息，添加spring boot redis start依赖
```
security:
  oauth2:
    client:
      client-id: client
      client-secret: client
    resource:
      user-info-uri: http://localhost:9001/userInfo
```

配置：定义RedisTokenStore对象，获取数据并解析成OAuth2AccessToken和OAuth2Authentication对象，如：

```
        @Bean
        @ConditionalOnMissingBean
        @Primary
        public DefaultTokenServices defaultTokenServices(TokenStore tokenStore){
            DefaultTokenServices defaultTokenServices=new DefaultTokenServices();
            defaultTokenServices.setTokenStore(tokenStore);
            return defaultTokenServices;
        }


        /**
         * 从Redis中获取token数据并解析
         * @param redisConnectionFactory
         * @return
         */
        @Bean
        @Conditional(ResourceServerTokenCondition.RedisTokenInfoCondition.class)
        @ConditionalOnMissingBean
        public RedisTokenStore redisTokenStore(RedisConnectionFactory redisConnectionFactory){
            RedisTokenStore tokenStore=new RedisTokenStore(redisConnectionFactory);
            tokenStore.setPrefix("orchid:auth-server:");
            return tokenStore;
        }
```


### 3.2、JwtTokenStore：从jwt中读取数据

Token信息签名加密后，存储在jwt令牌中。资源服务器校验时只需要获取到认证服务器签名时的秘钥，即可进行令牌的校验和解析。


参数：设置jwt签名秘钥key-value，或者获取jwt签名秘钥的端点信息key-uri，如：
```
security:
  oauth2:
    client:
      client-id: client
      client-secret: client
    resource:
      jwt:
        key-uri:  http://localhost:9001/oauth/token_key
#        key-value: classpath:pubkey.txt

```

解析步骤：
````
1、DefaultTokenServices对象，会调用JwtTokenStore对象获取OAuth2AccessToken和OAuth2Authentication对象

2、JwtTokenStore对象获取数据时，首先调用JwtAccessTokenConverter对象验证jwt，并解码出Jwt中的数据；

3、然后JwtAccessTokenConverter对象包含的DefaultAccessTokenConverter对象，将解析出的数据转换成OAuth2AccessToken和OAuth2Authentication对象
````


对象配置：定义JwtTokenStore,JwtAccessTokenConverter,DefaultAccessTokenConverter对象，如：

```
/**
 *2 Token信息存储在JWT中时
 */
@Configuration
@Conditional({TokenTypeCondition.JwtTokenCondition.class})
protected class JwtTokenConfig {

    private final ResourceServerProperties resourceServerProperties;

    public JwtTokenConfig(ResourceServerProperties resourceServerProperties) {
        this.resourceServerProperties = resourceServerProperties;
    }

    /**
     * 这里并不是对令牌的存储,他将访问令牌与身份验证进行转换
     * 在需要 {@link TokenStore} 的任何地方可以使用此方法
     *
     * @return TokenStore
     */
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    /**
     * jwt 令牌转换
     *
     * @return jwt
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setVerifierKey(getPubKey());


        DefaultAccessTokenConverter accessTokenConverter=new DefaultAccessTokenConverter();
        accessTokenConverter.setUserTokenConverter(new SubjectAttributeUserTokenConverter());

        converter.setAccessTokenConverter(accessTokenConverter);
        return converter;
    }

    /**
     * 非对称密钥加密，获取 public key。
     * 自动选择加载方式。
     *
     * @return public key
     */
    private String getPubKey() {
        // 如果本地没有密钥，就从授权服务器中获取
        return StringUtils.isEmpty(resourceServerProperties.getJwt().getKeyValue())
                ? getKeyFromAuthorizationServer()
                : resourceServerProperties.getJwt().getKeyValue();
    }

    /**
     * 本地没有公钥的时候，从服务器上获取
     * 需要进行 Basic 认证
     *
     * @return public key
     */
    private String getKeyFromAuthorizationServer() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, encodeClient());
        HttpEntity<String> requestEntity = new HttpEntity<>(null, httpHeaders);
        String pubKey = new RestTemplate()
                .getForObject(resourceServerProperties.getJwt().getKeyUri(), String.class, requestEntity);
        JSONObject body = JSONUtil.parseObj(pubKey);
        return body.getStr("value");
    }

    /**
     * 客户端信息
     *
     * @return basic
     */
    private String encodeClient() {
        return "Basic " + Base64.getEncoder().encodeToString((resourceServerProperties.getClientId()
                + ":" + resourceServerProperties.getClientSecret()).getBytes());
    }


}
```



实际配置：
**Spring Cloud OAuth2的自动配置类ResourceServerTokenServicesConfiguration已经自动配置了JwtTokenStore和JwtAccessTokenConverter对象**

要实现自定义jwt数据解析，只需要对自动导入的JwtAccessTokenConverter对象进行配置，如：

````
    @Bean
    JwtAccessTokenConverterConfigurer jwtAccessTokenConverterConfigurer(){
        return new JwtAccessTokenConverterConfigurer() {
            @Override
            public void configure(JwtAccessTokenConverter jwtAccessTokenConverter) {
                DefaultAccessTokenConverter defaultAccessTokenConverter=new DefaultAccessTokenConverter();
                defaultAccessTokenConverter.setUserTokenConverter(new SubjectAttributeUserTokenConverter());
                
                jwtAccessTokenConverter.setAccessTokenConverter(defaultAccessTokenConverter);
            }
        };
    }

````


### 3.2、JwkTokenStore：从jwt中读取数据
也是解析jwt数据，和JwtTokenStore不同的只是，获取jwt签名秘钥的方式不同，是通过jwk url获取秘钥信息。

内部实现也获取秘钥后，最终托管给JwtTokenStore对象来解码token数据。

**并且解析jwt时，需要jwt的头部中存在kid字段，目前Spring Cloud OAuth2认证服务中生成jwt时，头部没有该属性，所以解析不了。**


## 4、接口权限控制
资源服务接口权限控制，可以使用Spring Security提供的权限控制功能，需要预先分配给用户特定的权限，并指定各项操作执行所要求的权限。

用户请求执行某项操作时，Spring Security会先检查用户所拥有的权限是否符合执行该项操作所要求的权限，如果符合，才允许执行该项操作，否则拒绝执行该项操作。


### 4.1、开启级权限控制：
1、开启权限方法级权限控制:```@EnableGlobalMethodSecurity(prePostEnabled = true)```

2、使用注解控制方法的权限：
````
@PreAuthorize(spel)：在方法执行之前进行权限验证
@PostAuthorize(spel)：在方法执行之后进行权限验证
@PreFilter(spel)：在方法执行之前对方法集合类型的参数进行过滤
@PostFilter(spel)：在方法执行之后对方法返回的结合类型值进行过滤
````    


### 4.2、更多权限控制实现


## 5、参考


