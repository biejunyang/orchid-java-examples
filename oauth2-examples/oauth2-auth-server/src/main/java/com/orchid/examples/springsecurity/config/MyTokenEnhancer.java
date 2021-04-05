package com.orchid.examples.springsecurity.config;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义token增加处理
 * 往token对象中添加自定义信息
 */
public class MyTokenEnhancer implements TokenEnhancer {
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {

        if(oAuth2AccessToken instanceof DefaultOAuth2AccessToken){
            DefaultOAuth2AccessToken token=(DefaultOAuth2AccessToken) oAuth2AccessToken;
            //获取默认生成的token字符串
            String val = token.getValue();
            //自定义token字符串
            token.setValue(oAuth2Authentication.getName()+"---"+val);

            //Token对象中添加自定义信息
            Map<String, Object> map=new HashMap<>();
            map.put("name", oAuth2Authentication.getName());
            map.put("Principal", oAuth2Authentication.getPrincipal());
            token.setAdditionalInformation(map);
            return token;
        }

        return oAuth2AccessToken;
    }
}
