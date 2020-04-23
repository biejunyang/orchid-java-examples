# java-security-example
> 此 demo 主要演示了Java安全相关的内容。对Java 加密、解密常见的算法就行了说明。


## 1、Java加密解密介绍

常用的加密算法总体可以划分为两类：单向加密算法和双向加密算法。
单向加密算法：明文加密后不可以逆向解密，摘要算法可算作一种单向加密算法
双向加密算法：明白加密后可以逆向解密。他又可以分为两类：
````
    对称加密算法：加密和解密使用相同的秘钥
    非对称加密算法：加密和解密使用不同的秘钥，有一个密钥对分为公钥和私钥。公钥加密的必须用私钥解密、反之私钥加密需用公钥解密
````


## 2、Base64加密/解密
Base64实际并不是一种加密算法，而是程序经常使用到的一种编码方案。他能够将二进制数据(binary)编码成文本。它用 64 个可打印字符来表示二进制数据。这 64 个字符是：小写字母 a-z、大写字母 A-Z、数字 0-9、符号"+"、"/"（再加上作为垫字的"="，实际上是 65 个字符），其他所有符号都转换成这个字符集中的字符。  
Base编码和解码:
```java
    public static void main(String[] args) throws IOException {

        //Base64编码：二进制数据编码成可见文本
        String str=encode("hello".getBytes());
        System.out.println(str);

        //Base64解码：将Base64文本解码成二进制数据
        str=new String(decode(str));
        System.out.println(str);

    }

    public static String encode(byte[] bytes){
        BASE64Encoder base64Encoder=new BASE64Encoder();
        return base64Encoder.encode(bytes);
    }

    public static byte[] decode(String base64) throws IOException {
        BASE64Decoder base64Decoder=new BASE64Decoder();
        return base64Decoder.decodeBuffer(base64);
    }
```

## 3、摘要算法
摘要算法能够将任意长度大小的数据、经过一个单向的hash函数后转换成功固定长度的数据。产生的固定长度的数据可称为消息的消息摘要。
摘要算法主要用来做数据的一致性和完整性校验，以及在数字签名中使用。

### 摘要算法的特点
````
1、任意长度的消息经过相同的摘要算法后、产生的消息摘要的长度固定,如应用MD5算法摘要的消息有128个比特位，用SHA-1算法摘要的消息最终有160比特位的输出
2、相同消息的消息摘要必定相同、不同的消息产生的消息摘要必定不同。
3、摘要函数是单向不能逆向解密的，因为生成消息摘要时，并不是取完整的信息，而是取部分消息来生成。
````


注意：  
可以进行消息摘要的不仅仅是字符串数据，而可以使任意大小数据，如文件，字符串等。Java中实现都是二进制数据。
生成的消息摘要的字节数长度也是固定的，也是已二进制数据来展示。所以产生的消息摘要可以使用一定的编码规则转换为可视的文本数据，如Base64等


### 常见的摘要算法
````
MD5：message-digest algorithm 5 （信息-摘要算法）缩写，广泛用于加密和解密技术，常用于文件校验。校验？不管文件多大，经过MD5后都能生成唯一的MD5值。
SHA：SHA(Secure Hash Algorithm，安全散列算法），数字签名等密码学应用中重要的工具，SHA与MD5通过碰撞法都被破解了，但是SHA仍然是公认的安全加密算法，较之MD5更为安全。
HMAC：Hash Message Authentication Code，散列消息鉴别码，基于密钥的Hash算法的认证协议。消息鉴别码实现鉴别的原理是，用公开函数和密钥产生一个固定长度的值作为认证标识，用这个标识鉴别消息的完整性。使用一个密钥生成一个固定大小的小数据块，即MAC，并将其加入到消息中，然后传输。接收方利用与发送方共享的密钥进行鉴别认证等

BASE64的加密解密是双向的，可以求反解。 
MD5、SHA以及HMAC是单向加密，任何数据加密后只会产生唯一的一个加密串，通常用来校验数据在传输过程中是否被修改。其中HMAC算法有一个密钥，增强了数据传输过程中的安全性，强化了算法外的不可控因素。 
单向加密的用途主要是为了校验数据在传输过程中是否被修改。 
````

MD5算法
````
    MessageDigest md5=MessageDigest.getInstance("MD5");

    byte[] bytes=md5.digest("12312".getBytes());
    System.out.println(Base64.encode(bytes));


    bytes=md5.digest("12512".getBytes());
    System.out.println(Base64.encode(bytes));
    
````


## 4、对称加密算法（AES、DES、3DES）
对称加密算法是指加密和解密采用相同的密钥，是可逆的（即可解密）。
AES加密算法是密码学中的高级加密标准，采用的是对称分组密码体制，密钥长度的最少支持为128。AES加密算法是美国联邦政府采用的区块加密标准，这个标准用来替代原先的DES，已经被多方分析且广为全世界使用。
优点：加密速度快
缺点：密钥的传递和保存是一个问题，参与加密和解密的双方使用的密钥是一样的，这样密钥就很容易泄露。



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

https://tool.oschina.net/encrypt?type=2
 JSOE框架：https://blog.csdn.net/peterwanghao/article/details/98534636
 JWT说明：http://blog.leapoahead.com/2015/09/06/understanding-jwt/
 JWT官网：https://jwt.io/
 nimbus jwt框架：https://connect2id.com/products/nimbus-jose-jwt/examples
