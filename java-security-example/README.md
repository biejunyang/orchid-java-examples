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


## 2、Base64编码
Base64实际并不是一种加密算法，而是对二进制数据(字节流)的一种编码方案。他能够将二进制数据(binary)编码成文本。它用 64 个可打印字符来表示二进制数据。这 64 个字符是：小写字母 a-z、大写字母 A-Z、数字 0-9、符号"+"、"/"（再加上作为垫字的"="，实际上是 65 个字符），其他所有符号都转换成这个字符集中的字符。  
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

## 3、十六进制编码
计算机采用的是二进制的数据表示方法、而十六进制也是数据的一中展示方法。也是二进制数据的一中编码方案。

## 4、摘要算法
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
https://www.jianshu.com/p/b078282653b3
https://www.cnblogs.com/jfzhu/p/4020928.html
https://tool.oschina.net/encrypt?type=2
JSOE框架：https://blog.csdn.net/peterwanghao/article/details/98534636
JWT说明：http://blog.leapoahead.com/2015/09/06/understanding-jwt/
JWT官网：https://jwt.io/
nimbus jwt框架：https://connect2id.com/products/nimbus-jose-jwt/examples

https://www.cnblogs.com/AloneSword/p/3326750.html
