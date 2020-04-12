package com.orchid.examples.springsecurity.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;


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



    /**
     * 通过授权服务器远程校验Token
     * @return
     */
//    @Bean
//    public ResourceServerTokenServices tokenServices() {
//        RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
//        remoteTokenServices.setCheckTokenEndpointUrl("http://localhost:8080/oauth/check_token");
//        remoteTokenServices.setClientId("client");
//        remoteTokenServices.setClientSecret(""client"");
//        return remoteTokenServices;
//    }



//    private String publicKey=getPubKey();


//    @Bean
//    public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
//        return new JwtTokenStore(jwtAccessTokenConverter);
//    }


//    @Bean
//    public JwtAccessTokenConverter accessTokenConverter() {
//        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
//
//        // 设置对称加密秘钥
////        converter.setSigningKey("123456");
//
//        // 设置用于解码的非对称加密的公钥
//        converter.setVerifierKey(publicKey);
//
//        return converter;
//    }


    private String getPubKey() {
        Resource resource = new ClassPathResource("pubkey.txt");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            System.out.println("本地公钥");
            return br.lines().collect(Collectors.joining("\n"));
        } catch (IOException ioe) {
            return null;
        }


//        ObjectMapper objectMapper = new ObjectMapper();
//        String pubKey = new RestTemplate().getForObject("http://localhost:8080/oauth/token_key", String.class);
//        try {
//            Map map = objectMapper.readValue(pubKey, Map.class);
//            System.out.println("联网公钥");
//            return map.get("value").toString();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
    }


}
