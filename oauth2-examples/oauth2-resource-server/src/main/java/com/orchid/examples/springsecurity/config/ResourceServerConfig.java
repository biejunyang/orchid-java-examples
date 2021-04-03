package com.orchid.examples.springsecurity.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
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
//        resources.tokenStore(tokenStore(accessTokenConverter()));
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
//        remoteTokenServices.setClientSecret("client");
//        return remoteTokenServices;
//    }





//    @Bean
//    public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
//        return new JwtTokenStore(jwtAccessTokenConverter);
//    }


//    @Bean
//    public JwtAccessTokenConverter accessTokenConverter() throws NoSuchAlgorithmException, InvalidKeySpecException {
//        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
//
//        // 设置对称加密秘钥
////        converter.setSigningKey("123456");
//
//        // 设置用于解码的非对称加密的公钥
////        converter.setVerifierKey(publicKey);
//
//        String privateExponent = "3851612021791312596791631935569878540203393691253311342052463788814433805390794604753109719790052408607029530149004451377846406736413270923596916756321977922303381344613407820854322190592787335193581632323728135479679928871596911841005827348430783250026013354350760878678723915119966019947072651782000702927096735228356171563532131162414366310012554312756036441054404004920678199077822575051043273088621405687950081861819700809912238863867947415641838115425624808671834312114785499017269379478439158796130804789241476050832773822038351367878951389438751088021113551495469440016698505614123035099067172660197922333993";
//        String modulus = "18044398961479537755088511127417480155072543594514852056908450877656126120801808993616738273349107491806340290040410660515399239279742407357192875363433659810851147557504389760192273458065587503508596714389889971758652047927503525007076910925306186421971180013159326306810174367375596043267660331677530921991343349336096643043840224352451615452251387611820750171352353189973315443889352557807329336576421211370350554195530374360110583327093711721857129170040527236951522127488980970085401773781530555922385755722534685479501240842392531455355164896023070459024737908929308707435474197069199421373363801477026083786683";
//        String exponent = "65537";
//
//        converter.setVerifier(new RsaVerifier(new BigInteger(modulus), new BigInteger(exponent)));
//
//        return converter;
//    }


    //    private String publicKey=getPubKey();
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
