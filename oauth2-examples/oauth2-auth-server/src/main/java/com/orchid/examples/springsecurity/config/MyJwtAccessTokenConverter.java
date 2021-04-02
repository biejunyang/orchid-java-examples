package com.orchid.examples.springsecurity.config;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.system.UserInfo;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.stereotype.Component;

import java.util.*;

public class MyJwtAccessTokenConverter extends JwtAccessTokenConverter {


    /**
     * Token生成
     * @param accessToken
     * @param authentication
     * @return
     */
    @Override
    protected String encode(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;

//        UserInfo user = (UserInfo) authentication.getUserAuthentication().getPrincipal();
        Set<String> tokenScope = token.getScope();
        token.setScope(CollectionUtil.newHashSet("ROLE_ADMIN","ROLE_USER"));
        String scopeTemp = " ";
        if(tokenScope!=null&&tokenScope.size()>0){
            scopeTemp=tokenScope.iterator().next();
        }
        String scope =scopeTemp;
        //将额外的参数信息存入，用于生成token
        Map<String, Object> data = new HashMap<String, Object>(4){{
//            put("userId", user.getUserId());
//            put("username", user.getUsername());
//            put("email", user.getEmail());
//            put("roleDtos",user.getRoleDtos());
//            put("nickName", user.getNickName());
//            put("authorities", user.getAuthorities());
            put("scope",scope);
        }};
        //自定义TOKEN包含的信息
        token.setAdditionalInformation(data);
        return super.encode(accessToken, authentication);
    }

    /**
     * Token解析
     * @param token
     * @return
     */
    @Override
    protected Map<String, Object> decode(String token) {
        //解析请求当中的token  可以在解析后的map当中获取到上面加密的数据信息
        Map<String, Object> decode = super.decode(token);
        Long userId = (Long)decode.get("userId");
        String username = (String)decode.get("username");
        String email = (String)decode.get("email");
        String nickName = (String)decode.get("nickName");
        String scope = (String)decode.get("scope");
        List<GrantedAuthority> grantedAuthorityList=new ArrayList<>();
        //注意这里获取到的权限 虽然数据库存的权限是 "sys:menu:add"  但是这里就变成了"{authority=sys:menu:add}" 所以使用@PreAuthorize("hasAuthority('{authority=sys:menu:add}')")
        List<LinkedHashMap<String,String>> authorities =(List<LinkedHashMap<String,String>>) decode.get("authorities");
        for (LinkedHashMap<String, String> authority : authorities) {
            SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authority.getOrDefault("authority", "N/A"));
            grantedAuthorityList.add(grantedAuthority);
        }
//        UserInfo userInfo =new UserInfo(username,"N/A",userId, grantedAuthorityList);
//        userInfo.setNickName(nickName);
//        userInfo.setEmail(email);
        //需要将解析出来的用户存入全局当中，不然无法转换成自定义的user类
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("xxxx",null, grantedAuthorityList);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//        decode.put("user_name",userInfo);
        return decode;
    }
}
