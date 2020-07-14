# java-security-example
> 此 demo 主要演示了Java 微信公众号开发流程。


## 1、环境准备

公众号申请：https://mp.weixin.qq.com/?token=&lang=zh_CN

服务器准备：



## 2、公众号程序接入

### 2.1、填写服务器配置：
url：公众号程序用来接收微信消息和事件的接口URL。注意，微信公众号接口必须以http://或https://开头，分别支持80端口和443端口。

token：开发者可以任意填写，用作生成签名（该Token会和接口URL中包含的Token进行比对，从而验证安全性）

EncodingAESKey：由开发者手动填写或随机生成，将用作消息体加解密密钥

    
### 2.2、验证消息的确来自微信服务器
在上面配置的接口url的Get请求中，验证消息是否来自微信：

````
1）将token、timestamp、nonce三个参数进行字典序排序 
2）将排序后的三个字符串按顺序拼接成一个字符串后，进行sha1加密 
3）开发者获得加密后的字符串可与signature对比，相等则表示请求来源于微信,请求返回echostr参数、否则验证失败
````

```java
    
    @Value("${wx.token}")
    private String token;

    @GetMapping(produces = "text/plain;charset=utf-8")
    public String authGet(@RequestParam(name = "signature", required = false) String signature,
                          @RequestParam(name = "timestamp", required = false) String timestamp,
                          @RequestParam(name = "nonce", required = false) String nonce,
                          @RequestParam(name = "echostr", required = false) String echostr) {

        log.info("\n接收到来自微信服务器的认证消息：[{}, {}, {}, {}]", signature, timestamp, nonce, echostr);
        if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
            throw new IllegalArgumentException("请求参数非法，请核实!");
        }

        String [] strs=new String[]{token, timestamp, nonce};

        Arrays.sort(strs);

        String str= StrUtil.join("", strs);

        String result= DigestUtil.sha1Hex(str);

        if(signature.equals(result)){
            return echostr;
        }
        return "非法请求";
    }
```


### 2.3、接收微信发送的消息，实现业务逻辑
用户每次向公众号发送消息、或者产生自定义菜单、或产生微信支付订单等情况时，开发者填写的服务器配置URL将得到微信服务器推送过来的消息和事件，开发者可以依据自身业务逻辑进行响应，如回复消息。

在上面配置的接口url的POST请求中，解析微信发送过来的消息，并进行响应处理：

```java
    @PostMapping(produces = "application/xml; charset=UTF-8")
    public String post(@RequestBody String requestBody,
                       @RequestParam("signature") String signature,
                       @RequestParam("timestamp") String timestamp,
                       @RequestParam("nonce") String nonce,
                       @RequestParam("openid") String openid,
                       @RequestParam(name = "encrypt_type", required = false) String encType,
                       @RequestParam(name = "msg_signature", required = false) String msgSignature) {
        log.info("\n接收微信请求：[openid=[{}], [signature=[{}], encType=[{}], msgSignature=[{}],"
                + " timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
            openid, signature, encType, msgSignature, timestamp, nonce, requestBody);
        

        if (!wxService.checkSignature(timestamp, nonce, signature)) {
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }
        String out = null;
        if (encType == null) {
            // 明文传输的消息



        } else if ("aes".equalsIgnoreCase(encType)) {
            // aes加密的消息
        }

        log.debug("\n组装回复信息：{}", out);
        return null;
    }
```

## 3、消息管理

当普通微信用户向公众账号发消息时，微信服务器将POST消息的XML数据包到开发者填写的接口URL上。

XML数据包格式，如：
```xml
<xml>
  <ToUserName><![CDATA[toUser]]></ToUserName>
  <FromUserName><![CDATA[fromUser]]></FromUserName>
  <CreateTime>1348831860</CreateTime>
  <MsgType><![CDATA[text]]></MsgType>
  <Content><![CDATA[this is a test]]></Content>
  <MsgId>1234567890123456</MsgId>
</xml>
```

ToUserName: 开发者微信号

FromUserName: 发送方帐号（一个OpenID）

CreateTime: 消息创建时间 （整型）

Content: 消息内容

MsgId: 消息id、64位整型

MsgType: 消息类型，如：
```
    普通消息：text(文本消息)、image(图片消息)、voice(语言消息)、video(视频消息)、shortvideo(小视频消息)、location(位置消息)、link(链接消息)
    事件消息：event(事件消息)，如：关注、取消关注、扫描带参数二维码事件、上报地理位置事件、自定义菜单事件
```

被动消息回复:当用户发送消息给公众号时（或某些特定的用户操作引发的事件推送时），会产生一个POST请求，开发者可以在响应包（Get）中返回特定XML结构，来对该消息进行响应（现支持回复文本、图片、图文、语音、视频、音乐）。严格来说，发送被动响应消息其实并不是一种接口，而是对微信服务器发过来消息的一次回复。




## 4、微信Access Token获取
当公众号程序接收到微信发送的消息后，程序需要对消息进行处理。处理过程中可能会需要调用微信提供的各种接口(如：获取用户，创建菜单等)。调用微信接口时需要先获取微信颁发的令牌access_token。

access_token是公众号的全局唯一接口调用凭据，公众号调用各接口时都需使用access_token。开发者需要进行妥善保存。access_token的存储至少要保留512个字符空间。access_token的有效期目前为2个小时，需定时刷新，重复获取将导致上次获取的access_token失效。


## 4、自定义菜单管理





## 4、对称加密算法（AES、DES、3DES）
对称加密算法是指加密和解密采用相同的密钥，是双向加密算法，是可逆解密的。对称加密算法的特点是算法公开、计算量小、加密效率搞。

````
优点：加密速度快、使用长秘钥时的难破解性
缺点：密钥的传递和保存是一个问题，参与加密和解密的双方使用的密钥是一样的，这样密钥就很容易泄露。
````
常用的对称加密算法有AES、DES、3DES等算法。

其中AES加密算法是密码学中的高级加密标准，设计有3中秘钥长度(128、192、256)。比DES算法加密加密强度更大，更安全。AES加密算法是美国联邦政府采用的区块加密标准，这个标准用来替代原先的DES，已经被多方分析且广为全世界使用。
3DES算法是DES算法想AES算法过度的加密算法

### AES算法
1、生成AES加密解密的秘钥,长度可有128、192、256位。生成的秘钥位二进制数据(字节流)，可以使用一定的编码方案转化为字符串进行存储，如Base64编码、十六进制编码。
```java
    /**
     * 生成AES秘钥、并使用Base64编码字符串存储
     * @return
     */
    private static String getKeyAES() throws NoSuchAlgorithmException {
        //获取键的构造器
        KeyGenerator keyGenerator=KeyGenerator.getInstance("AES");

        //设置键键的位数
        keyGenerator.init(128);

        //生成键
        SecretKey secretKey=keyGenerator.generateKey();

        String base64Str= Base64.encode(secretKey.getEncoded());

        return base64Str;
    }
```


2、加载秘钥字符串生成秘钥对象
```java
    /**
     * 加载秘钥字符串，生成秘钥对象
     * @param keyStr
     * @return
     */
    private static SecretKey loadKeyAes(String keyStr){
        SecretKey secretKey=new SecretKeySpec(Base64.decode(keyStr), "AES");
        return secretKey;
    }
```
3、使用秘钥进行加密解密
加密：
```java
    public static byte[] encryptAES(byte[] source, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher=Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(source);
    }
```

解密：
```java
    public static byte[] dncryptAES(byte[] source, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher=Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(source);
    }
```

4、使用
```java
    String str="hello";
    String aesKey=getKeyAES();
    SecretKey secretKey=loadKeyAes(aesKey);

    //加密
    byte[] sources=encryptAES(str.getBytes(), secretKey);
    System.out.println(Base64.encode(sources));

    //解密
    str=new String(decryptAES(sources, secretKey));
    System.out.println(str);
```

## 5、非对称加密算法
非对称加密使用一个秘钥对来进行加密和解密。秘钥对中有两个秘钥可称为公钥和私钥。公钥和私钥需配对使用，公钥加密的只能用私钥解密、私钥加密的必须用公钥解密。

````
非对称加密算法相对于对称加密算法更加安全，解决对称加密算法中秘钥泄露的引起的问题。非对称加密算法中公钥是可以公开的，就算公钥和算法泄露，也无法获取到私钥，也就无法对公钥加密的数据进行解密。
比如客户断服务器发送消息，对称加密算法中，客户端和服务器使用相同的秘钥加密数据进行传输、秘钥泄露则可以对传输的数据进行解密。
非对称加密算法中，首先服务器生成秘钥对，并公布公钥，客户端获取到公钥后，使用公钥加密数据后进行传输，加密的数据只有保留私钥的服务器才可以解密，其他人就算知道公钥也无法解密数据。

````

消息发送方从消息接收方获取公布的公钥后，使用公钥对要发送的数据进行加密后传输。加密的数据只有持有是要的接收方才能解密。比如向银行发送消息。。。


### RSA非对称加密算法




### 非对称加密算的使用

非对称加密算法中、加密的数据字节数越大，需要使用的秘钥键的位数需越大，键的位数越大产生密钥对，以及加密和解密速度越慢，这是基于大素数非对称加密算法的缺陷。

由于非对称加密算法的复杂性，其加密解密的速度远没有对称加密算法的加密解密的速度快。所以结合两种加密算法使用，优缺点互补，到达时间平衡。

对称加密算法速度快，可用来加密较大的数据，然后使用非对称加密算法给加密数据的秘钥进行加密，解决了对称加密算法秘钥分发引起的秘钥泄露。



## 数字签名

数字签名是摘要算法和非对称加密算法的综合运用。消息发送方使用私钥将通信内容的消息摘要进行加密，然后将通信内容铭文和消息摘要的密文一起发送给接收方。

接收方使用发送者个公钥对密文就行解密、解析成功则一定是发送方发送的数据。然后对通信内容使用相同的摘要函数生成消息摘要和解密出来的数据进行比对，相同则消息是完整的，没有经过篡改。

### 数字签名的作用

1、识别或校验消息发送方的身份：公钥若是能够解密成功，则数据一定是由公钥对应的私钥拥有方签名发送的。私钥要保密非公开的，通过公钥就可以确定发送者的身份。

2、校验消息的完整新：对内容使用相同的摘要算法生成的消息摘要和解密的内容一致的话，说明通信内容没有经过篡改，否则一定是篡改了才导致消息摘要不一致。


### OAuth2.0中数字签名使用
1、认证服务器为某个用户生成令牌、并进行数字签名后返回给用户

2、用户持有令牌去访问受认证服务器保护的资源时，资源服务器校验令牌时，通过认证服务器获取的公钥去解密令牌中的数据，解密成功则一定是认证服务器授予的令牌、摘要相同则令牌没有被篡改。

3、解密并校验消息摘要是否一致的过程可称之为数字签名的验签。




### MD5withRSA数字签名实现





##  参考
https://mp.weixin.qq.com/?token=&lang=zh_CN
http://mp.weixin.qq.com/debug/cgi-bin/sandboxinfo?action=showinfo&t=sandbox/index
https://mp.weixin.qq.com/cgi-bin/home?t=home/index&lang=zh_CN&token=1003713443
http://mp.weixin.qq.com/debug/


为了识别用户，每个用户针对每个公众号会产生一个安全的OpenID，如果需要在多公众号、移动应用之间做用户共通，则需前往微信开放平台，将这些公众号和应用绑定到一个开放平台账号下，绑定后，一个用户虽然对多个公众号和应用有多个不同的OpenID，但他对所有这些同一开放平台账号下的公众号和应用，只有一个UnionID，可以在用户管理-获取用户基本信息（UnionID机制）文档了解详情。
