package com.orchid.examples.weixin.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@Slf4j
//@AllArgsConstructor
@RestController
@RequestMapping("/mp/portal")
public class WxPortalController {

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


        String out = null;
        if (encType == null) {
            // 明文传输的消息



        } else if ("aes".equalsIgnoreCase(encType)) {
            // aes加密的消息
        }

        log.debug("\n组装回复信息：{}", out);
        return null;
    }

//    private WxMpXmlOutMessage route(WxMpXmlMessage message) {
//        try {
//            return this.messageRouter.route(message);
//        } catch (Exception e) {
//            log.error("路由消息时出现异常！", e);
//        }
//
//        return null;
//    }

}
