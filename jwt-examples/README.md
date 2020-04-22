# jwt-examples
> 此 demo 主要演示了Java 中JWT的使用


## 1、JOSE(JavaScript对象签名和加密)框架介绍
JOSE是一个框架，旨在提供一种在各方之间安全传递消息的方法。JOSE框架中定义了一系列的规范来实现此目的：
````
    JSON Web Token (JWT)：JSON Web令牌（RFC7519），定义了一种可以签名或加密的标准格式； 
    JSON Web Signature (JWS)：JSON Web签名（RFC7515） ， 定义对JWT进行数字签名的过程； 
    JSON Web Encryption (JWE)：JSON Web加密（RFC7516） ， 定义加密JWT的过程；  
    JSON Web Algorithm（JWA）：JSON Web算法（RFC7518） ， 定义用于数字签名或加密的算法列表；  
    JSON Web Key (JWK)：JSON Web密钥（RFC7517） ， 定义加密密钥和密钥集的表示方式；  
````

## 2、Nimbus JOSE JWT
nimbus-jose-jwt是一个操作jwt的Java类库，对JOSE框架中的规范进行了一定的实现。可以通过JOSE中定义的对象(JWS、JWT)在请求之间安全传输数据。

### pom依赖添加
```xml
<dependency>
    <groupId>com.nimbusds</groupId>
    <artifactId>nimbus-jose-jwt</artifactId>
    <version>8.3</version>
</dependency>
```

## 3、JWS(Json Web Signature)实现

JWS使用了HMAC算法或者数据签名(digital signature)对需要传输的数据进行安全保护。保护的数据可以纯文本、json、二进制流、甚至是JWT对象等数据。

### JWS对象的创建
```java
    //头部
    JWSHeader jwsHeader=new JWSHeader(JWSAlgorithm.HS256);
    
    
    //负载：需要安全保护的数据，
    Payload payload=new Payload("hello");
    
    //        Payload payload=new Payload(new JSONObject(new HashMap<>()));
    
    //签名秘钥：
    byte[] sharedSectre=new byte[32];
    new SecureRandom().nextBytes(sharedSectre);
    JWSSigner jwsSigner=new MACSigner(sharedSectre);
    
    
    //创建JWS对象，并对其签名
    JWSObject jwsObject=new JWSObject(jwsHeader, payload);
    jwsObject.sign(jwsSigner);
    
    //序列化jws对象传输
    String jws=jwsObject.serialize();
```

### JWS对象的解析和验证
```java
//解析jws字符串
jwsObject = JWSObject.parse(jws);

//验签
    JWSVerifier verifier = new MACVerifier(sharedSecret);
    
    if(jwsObject.verify(verifier)){
        String payloadContent=jwsObject.getPayload().toString();
        System.out.println(payloadContent);
    }else{
        System.out.println("签名错误");
    }
```
### JWS说明
````
1、JWS对象主要由两部分组成：
    头部(JWSHeader)：头部中包含了数字签名使用的加密算法
    负载(Payload)：Jws对象需要安全保护的实际数据，可以使String，JSONObject、字节流、BASE64URL加密的数据等

2、JWS对象签名后，需要使用秘钥对其就行数字签名
````

### JWS对象非对称加密算法签名
```java
//1、创建RSA算法密钥对
    RSAKey rsaJWK=new RSAKeyGenerator(2048)
            .keyID("123").generate();
    
    
    //2、创建头部、主要要指定秘钥对的id
    JWSHeader jwsHeader=new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("123").build();
    
    //3、负载
    Payload payload=new Payload("hello");
    
    //JWS对象
    JWSObject jwsObject=new JWSObject(jwsHeader, payload);
    
    //签名对象
    JWSSigner jwsSigner=new RSASSASigner(rsaJWK);
    
    
    //数字签名
    jwsObject.sign(jwsSigner);
    
    String jws=jwsObject.serialize();
    System.out.println(jws);
    
    //解析jws字符串
    jwsObject = JWSObject.parse(jws);
    
    //验签对象
    JWSVerifier verifier = new RSASSAVerifier(rsaJWK.toRSAPublicKey());
    
    if(jwsObject.verify(verifier)){
        String payloadContent=jwsObject.getPayload().toString();
        System.out.println(payloadContent);
    }else{
        System.out.println("签名错误");
    }
```
注意：
```
RSAKey：RSA非对称加密算法、生成密钥对用来加密和解密、JWSHeader对象需指定秘钥对的id
RSASSASigner：RSA签名对象、对jws对象进行数字签名
RSASSAVerifier：验签对象
```

## 4、JWT(Json Web Token)实现
JWT是一种基于json数据传出的Web开放标准，他定义了一种可以签名或加密的标准格式。

### JWT创建和解析
```java
    //头部
    JWSHeader jwsHeader=new JWSHeader(JWSAlgorithm.HS256);


    //负载
    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .subject("alice")
            .issuer("https://c2id.com")
            .expirationTime(new Date(System.currentTimeMillis() + 60 * 1000))
            .claim("other", new Object())
            .build();


    //签名秘钥：
    byte[] sharedSecret=new byte[32];
    new SecureRandom().nextBytes(sharedSecret);
    JWSSigner jwsSigner=new MACSigner(sharedSecret);


    //创建JWT对象，并对其签名
    SignedJWT signedJWT=new SignedJWT(jwsHeader, claimsSet);
    signedJWT.sign(jwsSigner);

    //序列化jws对象传输
    String jwt=signedJWT.serialize();

    System.out.println(jwt);
//        jws=jws.substring(0, jws.length()-1);



    //解析jws字符串
     signedJWT= SignedJWT.parse(jwt);

    //验签对象
    JWSVerifier verifier = new MACVerifier(sharedSecret);

    if(signedJWT.verify(verifier)){
        String subject=signedJWT.getJWTClaimsSet().getSubject();
        Date exprireDate=signedJWT.getJWTClaimsSet().getExpirationTime();
        String payloadContent=signedJWT.getPayload().toString();
        System.out.println(subject);
        System.out.println(exprireDate);
        System.out.println(payloadContent);
    }else{
        System.out.println("签名错误");
    }
```

### 说明
JWT和JWS的实现比较相似，实际就是JWT中对需要加密和验证的数据进行了标准的定义，Payload是json数据格式，并且提供了标准的声明格式：
````
    iss: jwt签发者
    sub: 面向的用户(jwt所面向的用户)
    aud: 接收jwt的一方
    exp: 过期时间戳(jwt的过期时间，这个过期时间必须要大于签发时间)
    nbf: 定义在什么时间之前，该jwt都是不可用的.
    iat: jwt的签发时间
    jti: jwt的唯一身份标识，主要用来作为一次性token,从而回避重放攻击。
````

### JWT的非对称加密签名
和JWS实现相似



## 5、JWE(Json Web Encryption )
Json Web数据加密。JWS、JWT中负载Payload中的数据默认是没有加密的，传输时只要使用base64算法转换成字符串了。使用JWE可以传输数据进行加密。

### JWT payload的加密和解析
```
public static void testEncryptJWTClaim() throws JOSEException, ParseException {
    //1、创建RSA算法密钥对
    RSAKey rsaJWK=new RSAKeyGenerator(2048)
            .keyID("123").generate();


    //创建JWE头部:指定加密算法
    JWEHeader jweHeader=new JWEHeader(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A128CBC_HS256);

    // JWT payload
    Date now = new Date();
    JWTClaimsSet jwtClaims = new JWTClaimsSet.Builder()
            .issuer("https://openid.net")
            .subject("alice")
            .audience(Arrays.asList("https://app-one.com", "https://app-two.com"))
            .expirationTime(new Date(now.getTime() + 1000*60*10)) // expires in 10 minutes
            .notBeforeTime(now)
            .issueTime(now)
            .jwtID(UUID.randomUUID().toString())
            .build();

    System.out.println(jwtClaims.toJSONObject());


    //创建加密的jwt对象
    EncryptedJWT encryptedJWT=new EncryptedJWT(jweHeader, jwtClaims);

    //创建加密对象
    JWEEncrypter encrypter=new RSAEncrypter(rsaJWK.toRSAPublicKey());

    //加密
    encryptedJWT.encrypt(encrypter);

    String jwt=encryptedJWT.serialize();

    System.out.println(jwt);
    
    //解析加密后的jwt数据
    encryptedJWT=EncryptedJWT.parse(jwt);

    //解密对象
    JWEDecrypter decrypter=new RSADecrypter(rsaJWK.toPrivateKey());

    //解密
    encryptedJWT.decrypt(decrypter);

    // Retrieve JWT claims
    System.out.println(encryptedJWT.getJWTClaimsSet().getIssuer());
    System.out.println(encryptedJWT.getJWTClaimsSet().getSubject());
    System.out.println(encryptedJWT.getJWTClaimsSet().getAudience().size());
    System.out.println(encryptedJWT.getJWTClaimsSet().getExpirationTime());
    System.out.println(encryptedJWT.getJWTClaimsSet().getNotBeforeTime());
    System.out.println(encryptedJWT.getJWTClaimsSet().getIssueTime());
    System.out.println(encryptedJWT.getJWTClaimsSet().getJWTID());
}
```
````注意以上只是对JWT中的payload并没有对jwt进行签名 ````

### JWT 签名并且加密
```java
public static void testSignAndEncryptJWTClaim() throws JOSEException, ParseException {
    //创建签名的RSA算法密钥对
    RSAKey rsaJWK=new RSAKeyGenerator(2048)
            .keyID("123").generate();

    //签名jwt对象
    SignedJWT signedJWT=new SignedJWT(
            new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("123").build(),
            new JWTClaimsSet.Builder() .subject("alice")
                    .issueTime(new Date())
                    .issuer("https://c2id.com")
                    .build());

    //签名对象
    JWSSigner jwsSigner=new RSASSASigner(rsaJWK);

    //签名
    signedJWT.sign(jwsSigner);


    //创建加密的秘钥对
    RSAKey rsaJWE=new RSAKeyGenerator(2048)
            .keyID("456").generate();


    //创建JWE对象、加密的内容就是签名的jwt对象
    JWEObject jweObject = new JWEObject(
            new JWEHeader.Builder(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A128CBC_HS256)
                    .contentType("JWT") // required to indicate nested JWT
                    .build(),
            new Payload(signedJWT));

    //创建加密对象
    JWEEncrypter encrypter=new RSAEncrypter(rsaJWE.toRSAPublicKey());

    //加密
    jweObject.encrypt(encrypter);

    String jwe=jweObject.serialize();

    System.out.println(jwe);


    //解析加密后的jwe数据
    jweObject=JWEObject.parse(jwe);

    //解密对象
    JWEDecrypter decrypter=new RSADecrypter(rsaJWE.toPrivateKey());

    //解密
    jweObject.decrypt(decrypter);

    //解析出签名的jwt对象
    signedJWT=jweObject.getPayload().toSignedJWT();

    JWSVerifier jwsVerifier=new RSASSAVerifier(rsaJWK.toRSAPublicKey());
    if(signedJWT.verify(jwsVerifier)){

        System.out.println(signedJWT.getJWTClaimsSet().getIssuer());
        System.out.println(signedJWT.getJWTClaimsSet().getSubject());
    }else{
        System.out.println("签名错误");
    }
}
```



## 6、JWK(Json Web Key)
JWK定义加密密钥和密钥集的表示方式。秘钥可能由其他环境生成，并JWK的形式对需要解密方进行开发。解密方获取JWK后，可以数据进行解密。
JWK是已json串的形式展示秘钥的相关信息


### JWK对象的创建
```java
    // Generate 2048-bit RSA key pair in JWK format, attach some metadata
    RSAKey jwk = new RSAKeyGenerator(2048)
        .keyUse(KeyUse.SIGNATURE) // indicate the intended use of the key
        .keyID(UUID.randomUUID().toString()) // give the key a unique ID
        .generate();
    
    // Output the private and public RSA JWK parameters
    System.out.println(jwk);
    
    // Output the public RSA JWK parameters only
    System.out.println(jwk.toPublicJWK());
```

```
System.out.println(jwk)：展示公钥和秘钥的相关信息
System.out.println(jwk.toPublicJWK()):仅展示公钥信息
```

### JWK对象获取并使用JWK对象解密
```java
    RSAKey jwk=(RSAKey)JWK.parse(map.get("jwk"));
        
    SignedJWT jwt=SignedJWT.parse(map.get("jwt"));

    JWSVerifier jwsVerifier=new RSASSAVerifier(jwk.toRSAPublicKey());
    if(jwt.verify(jwsVerifier)){
        System.out.println(jwt.getJWTClaimsSet().getSubject());
    }else{
        System.out.println("验签失败");
    }
```

### JWKSet秘钥集

JWKSet秘钥集获取
````
1、从本地或者远程json文件加载JwkSet
    // Load JWK set from filesystem
    JWKSet localKeys = JWKSet.load(new File("my-key-store.json"));

    // Load JWK set from URL
    JWKSet publicKeys = JWKSet.load(new URL("https://c2id.com/jwk-set.json"));

2、从远程url获取
    JWKSet publicKeys = JWKSet.load(new URL("https://c2id.com/jwk-set.json"));
    
3、从Java keyStore中加载JwkSet
    // Specify the key store type, e.g. JKS
    KeyStore keyStore = KeyStore.getInstance("JKS");
    
    // If you need a password to unlock the key store
    char[] password = "secret".toCharArray();
    
    // Load the key store from file
    keyStore = keyStore.load(new FileInputStream("myKeyStore.jks", password);
    
    // Extract keys and output into JWK set; the secord parameter allows lookup 
    // of passwords for individual private and secret keys in the store
    JWKSet jwkSet = JWKSet.load(keyStore, null);
    
````

JWK Selector选择器、从JWKSet秘钥集中匹配

````
    List<JWK> matches = new JWKSelector(
        new JWKMatcher.Builder()
            .keyType(KeyType.RSA)
            .keyID("123456")
            .build()
        ).select(jwkSet);
    
    System.out.println("Found " + matches.size() + " matching JWKs");
````




### 1、FilterSecurityInterceptor(安全认证过滤器)：
Spring Security过滤器链中的最后一个过滤器，其作用是保护请求的资源，校验当前请求是否已经过认证，并且用户是否有权限访问该请求。未经过认证则抛出AuthenticationException异常，权限不够则抛出AccessDenyException异常。其实现流程：
````
通过SecurityContextHolder.getContext().getAuthentication()获取当请求上线文中的Authentication对象。通过该Authentication对象中的认证状态，和用户的角色，权限信息判断是否有权限访资源。
````

### 2、SecurityContextPersistenceFilter(安全上下文持久化过滤器):
当前请求的安全上的准备和获取。请求开始时从配置好的 SecurityContextRepository中获取 SecurityContext，然后把它设置给 SecurityContextHolder。在请求完成后将 SecurityContextHolder 持有的 SecurityContext 再保存到配置好的 SecurityContextRepository，同时清除 securityContextHolder 所持有的 SecurityContext；
SecurityContext中存储了用户的认证信息，SecurityContextRepository默认的实现为HttpSessionSecurityContextRespotiory，及默认从session对象获取认证信息。

 
## 3、Spring Security JWT 实现原理
````
1、JWT鉴权中用户的认证信息Authentication对象不是存在在Session对象中，而是使用JWT Token来承载，发送给客户端。后端并不需要存储用户状态，客户端访问全资源时通过携带授权的JWT Token来访问资源。

2、通过实现JWT的鉴权过滤器，校验请求访问时携带的Token是否有效，有效则表示认证通过，然后解析Token获取token对应的用户认证信息，并且保存在SecurityContext中。<br/>
   注意token校验通过后，需要将token对应的认证信息保存到SecurityContext中，因为Spring Security最后的FilterSecurityInterceptor需要从中获取认证Authentication对象进行进一步权限认证。

3、实现登录请求端点，返回JWT Token.
````


## 4、Jwt鉴权实现

### Jwt权限认证过滤器JwtAuthorizationFilter
校验用户请求中携带的Token,Token校验成功，则解析Token获取对应的用户认证信息Authentication对象，并放到安全上下文中，校验失败则返回错误码


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
| hasRole([role])| 当前用户是否拥有指定角色。|
| hasAnyRole([role1,role2])| 多个角色是一个以逗号进行分隔的字符串。如果当前用户拥有指定角色中的任意一个则返回true。|  
| hasAuthority([auth])| 等同于hasRole|
| hasAnyAuthority([auth1,auth2])| 等同于hasAnyRole|
| Principle| 代表当前用户的principle对象|
| authentication| 直接从SecurityContext获取的当前Authentication对象|
| permitAll| 总是返回true，表示允许所有的|
| denyAll| 总是返回false，表示拒绝所有的。|
| isAnonymous()| 当前用户是否是一个匿名用户。|
| isRememberMe()| 表示当前用户是否是通过Remember-Me自动登录的。|
| isAuthenticated()| 表示当前用户是否已经登录认证成功了。|
| isFullyAuthenticated()| 如果当前用户既不是一个匿名用户，同时又不是通过Remember-Me自动登录的，则返回true。。|

```
注意：权限权限名称需要已"ROLE_"开头
```` 

### access动态url认证
```java
        http
            .authorizeRequests()
                .anyRequest().authenticated()
                .anyRequest().access("@authorityService.hasPermission(request,authentication)");
```




### 
4、示例
```java
@PreAuthorize("hasRole('ROLE_ADMIN')")
public void addUser(User user) {
   ...
}

@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
public User find(int id) {
   return null;
}

@PreAuthorize("#id<10")
public User find(int id) {
   return null;
}

@PreAuthorize("principal.username.equals(#username)")
public User find(String username) {
   return null;
}

@PreAuthorize("#user.name.equals('abc')")
public void add(User user) {
   ...
}

@PostAuthorize("returnObject.id%2==0")
public User find(int id) {
   ...
   return user;
}

@PostFilter("filterObject.id%2==0")
public List<User> findAll() {
   List<User> userList = new ArrayList<User>();
   ...
   return userList;
}

@PreFilter(filterTarget="ids", value="filterObject%2==0")
public void delete(List<Integer> ids, List<String> usernames) {
   ...
}


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
	


