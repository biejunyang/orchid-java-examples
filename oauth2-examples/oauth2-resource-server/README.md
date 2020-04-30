# oauth2-resource-server
> 此 demo 主要演示了 Spring Security OAuth2.0中Resource Server资源服务器的实现。

## 1、Resource Server介绍
资源服务器的作用就是保护资源，验证令牌是否有效，有效则允许访问受保护的资源


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




## 3、Resource Server配置

### 3.1、@EnableResourceServer开启资源服务器自动配置
@EnableResourceServer注解，表示声明一个资源服务器保护资源，导入Oauth2 Res ource Server的默认配置。该注解导入的配置类：

    ResourceServerConfiguration：WebSecurityConfigerAdater的实例，顺序为3，定义了默认的安全保护机制。默认所有请求受保护，如：

    OAuth2AuthenticationProcessingFilter：开启了OAuth2请求认证的过滤器，必须要使用令牌才能访问受保护资源


### 3.2、自定义配置
定义一个配置类，继承ResourceServerConfigurerAdapter:

```java
@EnableResourceServer
@Configuration
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        super.configure(http);
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        super.configure(resources);
//        resources.tokenServices(tokenServices());
    }

}

```
HttpSecurity：定义资源保护的安全规则

ResourceServerSecurityConfigurer资源服务器相关参数配置，如：
``
    •tokenServices：ResourceServerTokenServices 类的实例，用来实现令牌的校验逻辑，默认实现为DefaultTokenServices。
    •resourceId：这个资源服务的ID，这个属性是可选的，但是推荐设置并在授权服务中进行验证
    •tokenExtractor 令牌提取器用来提取请求中的令牌
    •请求匹配器，用来设置需要进行保护的资源路径，默认的情况下是受保护资源服务的全部路径
    •受保护资源的访问规则，默认的规则是简单的身份验证（plain authenticated）
    •其他的自定义权限保护规则通过 HttpSecurity 来进行配置
``


## 4、令牌校验

资源服务器的主要作用就是校验令牌的有效性，能进行资源权限的控制。spring-security-oauth2-autoconfigure中资源服务器的实现中，提供了集中令牌解析的方式，并进行了自动配置。

会根据配置文件中参数的不同，使用不同的方式来进行令牌的校验，如：

### 4.1、TokenInfoCondition参数环境
使用认证服务器的令牌校验端点服务/oauth/check_tokan来校验令牌。这种方式无论认证服务器使用哪个令牌存储方式(内存、redis、jdbc、jwt)时，都可以用校验令牌，前提是认证服务器提供了对应的端点。并且每次令牌校验时都会向认证服务器发送校验请求。

application.yml参数配置如：
```yaml
security:
  oauth2:
    resource:
      token-info-uri: http://localhost:8080/oauth/check_token
    client:
      client-id: client
      client-secret: client

```


### 4.2、JwtTokenCondition参数环境
此种方式只使用与jwt令牌的校验，它会根据配置参数获取到JWT令牌的签名秘钥，然后对令牌进行解析。并且这种方式进行令牌校验时，不需要想认证服务发送校验请求。

application.yml参数配置如：
```yaml
security:
  oauth2:
    resource:
      jwt:
        key-uri:  http://localhost:8080/oauth/token_key
#        key-value: classpath:pubkey.txt
    client:
      client-id: client
      client-secret: client

```

key-uri：指定认证服务器获取签名秘钥的端点
key-value：直接指定秘钥内容，或秘钥文件





### 4.3、JwkCondition参数环境
此种方式只使用与jwt令牌的校验，它会根据配置参数获取到JWT令牌的签名公钥信息，然后对令牌进行解析。并且这种方式进行令牌校验时，不需要想认证服务发送校验请求。

application.yml参数配置如：
```yaml
security:
  oauth2:
    resource:
      jwk:
        key-set-uri: http://localhost:8080/.well-known/jwks.json
    client:
      client-id: client
      client-secret: client

```

key-set-uri：JWK(json web key)秘钥信息公开端点




## 5、令牌校验手动实现
令牌的颁发是由认证服务器来完成，所以resource server解析时要根据授权服务器的令牌存储方式，使用相同的规则来解析和解码Token，如：
````
a、授权服务器使用InMemoryTokenStorel来存储Token到内存中，若是资源服务器和授权服务器是一个应用，和可以直接使用DefaultTokenService从内存中获取Token；
    若是分离的则必须要通过RemoteTokenService获取https请求认证服务器的/oauth/check_token端点来校验Token.

b、授权服务器使用JdbcTokenStore存储令牌到数据库中，则资源服务器需要定义相同的Token存储方式JdcbTokenStore，然后从数据库中获取Token并校验。

c、授权服务器使用JWT令牌时，资源服务器只需要在本地对令牌校验。检验是会对令牌进行验签，令牌的签名方式不一样，验签规则不一样。
````

资源服务器令牌的解析和通过令牌获取用户认证信息都是有ResourceServerTokenServices对象来实现的。spring secuiry oauth2中默认提供了两个实现对象：
````
DefaultTokenServices(本地校验)：在本地进行令牌的解码，验证，不需要访问授权服务器，如使用JdbcTokenStore和JWT令牌校验时，都是使用该对象，只不过指定的Token存储不一样(jdcb和jwt)

RemoteTokenServices (远程授权服务器校验)：通过 HTTP 请求来解码令牌，每次都请求授权服务器端点 /oauth/check_token
````


### 5.1、DefaultTokenServices本地令牌校验
当认证服务器使用redis、jwt、jdbc存储令牌时，资源服务器都可以使用DefaultTokenServices对象进行本地令牌校验，但是需要设置与认证服务器相同的TokenStore对象，

如使用jwt令牌时：

```java
    
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenStore(tokenStore(accessTokenConverter()));
    }
        
    
    @Bean
    public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
        return new JwtTokenStore(jwtAccessTokenConverter);
    }


    @Bean
    public JwtAccessTokenConverter accessTokenConverter() throws NoSuchAlgorithmException, InvalidKeySpecException {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();

        // 设置对称加密秘钥
//        converter.setSigningKey("123456");

        // 设置用于解码的非对称加密的公钥
//        converter.setVerifierKey(publicKey);

        String privateExponent = "3851612021791312596791631935569878540203393691253311342052463788814433805390794604753109719790052408607029530149004451377846406736413270923596916756321977922303381344613407820854322190592787335193581632323728135479679928871596911841005827348430783250026013354350760878678723915119966019947072651782000702927096735228356171563532131162414366310012554312756036441054404004920678199077822575051043273088621405687950081861819700809912238863867947415641838115425624808671834312114785499017269379478439158796130804789241476050832773822038351367878951389438751088021113551495469440016698505614123035099067172660197922333993";
        String modulus = "18044398961479537755088511127417480155072543594514852056908450877656126120801808993616738273349107491806340290040410660515399239279742407357192875363433659810851147557504389760192273458065587503508596714389889971758652047927503525007076910925306186421971180013159326306810174367375596043267660331677530921991343349336096643043840224352451615452251387611820750171352353189973315443889352557807329336576421211370350554195530374360110583327093711721857129170040527236951522127488980970085401773781530555922385755722534685479501240842392531455355164896023070459024737908929308707435474197069199421373363801477026083786683";
        String exponent = "65537";

        converter.setVerifier(new RsaVerifier(new BigInteger(modulus), new BigInteger(exponent)));

        return converter;
    }
```


### 5.2、RemoteTokenServices远程令牌校验
资源服务器令牌校验时，想远程认证服务器发送令牌校验请求，

```java
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenServices(tokenServices());
    }



    /**
     * 通过授权服务器远程校验Token
     * @return
     */
    @Bean
    public ResourceServerTokenServices tokenServices() {
        RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
        remoteTokenServices.setCheckTokenEndpointUrl("http://localhost:8080/oauth/check_token");
        remoteTokenServices.setClientId("client");
        remoteTokenServices.setClientSecret("client");
        return remoteTokenServices;
    }
```


## 6、参考
JSOE框架：https://blog.csdn.net/peterwanghao/article/details/98534636

nimbus jwt框架：https://connect2id.com/products/nimbus-jose-jwt/examples

JWT说明：http://blog.leapoahead.com/2015/09/06/understanding-jwt/

JWT官网：https://jwt.io/

