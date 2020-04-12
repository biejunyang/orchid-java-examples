package com.orchid.examples.springsecurity.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

@EnableAuthorizationServer
@Configuration
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {


//    @Autowired
//    private DataSource dataSource;

    @Autowired
    private AuthenticationManager authenticationManager;


    @Autowired
    private TokenStore tokenStore;


    @Autowired
    private AccessTokenConverter accessTokenConverter;

    /**
     * 1、授权服务端点设置
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager) //指定用户认证管理器
	            .tokenStore(tokenStore)
                .accessTokenConverter(accessTokenConverter);

        // 配置TokenServices参数
//        DefaultTokenServices tokenServices = (DefaultTokenServices) endpoints.getDefaultAuthorizationServerTokenServices();
//        tokenServices.setTokenStore(endpoints.getTokenStore());
//        tokenServices.setSupportRefreshToken(true);
//        // 复用refresh token
//        tokenServices.setReuseRefreshToken(false);
//        tokenServices.setClientDetailsService(endpoints.getClientDetailsService());
//        tokenServices.setTokenEnhancer(endpoints.getTokenEnhancer());
//        tokenServices.setAccessTokenValiditySeconds((int) TimeUnit.SECONDS.toSeconds(1)); // 1天
//        endpoints.tokenServices(tokenServices);


    }



    /**
     * 2、授权服务端点安全约束
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
            .tokenKeyAccess("permitAll()")// jwt获取令牌key端点不要认证
            .checkTokenAccess("isAuthenticated()")//校验令牌端点需要授权过
                .allowFormAuthenticationForClients();
    }


    /**
     * 客户端设置
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//        1、内存中设置客户端信息inMemory()，注意授权码授权时，必须设置好重定向地址
        clients
            .inMemory().withClient("client").secret("{noop}client")
                .authorizedGrantTypes("client_credentials", "authorization_code", "refresh_token", "password", "implicit")
                .scopes("read","write")
                .redirectUris("http://baidu.com", "http://localhost:8083/client/login", "http://localhost:8083/client/hello","http://localhost:8084/client2/login")
                .accessTokenValiditySeconds(600_000_000).and()

                .withClient("client1").secret("{noop}client1")
                .authorizedGrantTypes("client_credentials", "authorization_code", "refresh_token", "password", "implicit")
                .scopes("read","write")
                .redirectUris("http://localhost:8085/client1/login")
                .accessTokenValiditySeconds(600_000_000).autoApprove(true).and()

                .withClient("client2").secret("{noop}client2")
                .authorizedGrantTypes("client_credentials", "authorization_code", "refresh_token", "password", "implicit")
                .scopes("read","write")
                .redirectUris("http://localhost:8086/client2/login")
                .accessTokenValiditySeconds(600_000_000).autoApprove(true);


        //2、从jdbc数据源中获取客户端信息，默认的表结构中
//        clients.jdbc(dataSource).passwordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder());
//        clients.withClientDetails(new JdbcClientDetailsService(dataSource));


//        3、自定义通过clientId获取客户端信息
//        clients.withClientDetails(new ClientDetailsService() {
//            @Override
//            public ClientDetails loadClientByClientId(String s) throws ClientRegistrationException {
//
//                return null;
//            }
//        });
    }




}




