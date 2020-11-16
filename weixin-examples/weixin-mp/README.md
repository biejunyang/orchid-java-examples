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






##  参考
https://github.com/Wechat-Group/WxJava/wiki/%E5%85%AC%E4%BC%97%E5%8F%B7%E5%BC%80%E5%8F%91%E6%96%87%E6%A1%A3
https://mp.weixin.qq.com/?token=&lang=zh_CN
http://mp.weixin.qq.com/debug/cgi-bin/sandboxinfo?action=showinfo&t=sandbox/index
https://mp.weixin.qq.com/cgi-bin/home?t=home/index&lang=zh_CN&token=1003713443
http://mp.weixin.qq.com/debug/


为了识别用户，每个用户针对每个公众号会产生一个安全的OpenID，如果需要在多公众号、移动应用之间做用户共通，则需前往微信开放平台，将这些公众号和应用绑定到一个开放平台账号下，绑定后，一个用户虽然对多个公众号和应用有多个不同的OpenID，但他对所有这些同一开放平台账号下的公众号和应用，只有一个UnionID，可以在用户管理-获取用户基本信息（UnionID机制）文档了解详情。
