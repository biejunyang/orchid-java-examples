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


##  参考
 JSOE框架：https://blog.csdn.net/peterwanghao/article/details/98534636
 JWT说明：http://blog.leapoahead.com/2015/09/06/understanding-jwt/
 JWT官网：https://jwt.io/
 nimbus jwt框架：https://connect2id.com/products/nimbus-jose-jwt/examples
