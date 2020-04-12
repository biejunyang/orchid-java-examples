package com.orchid.samples.oauth2client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;


@EnableOAuth2Client
@Configuration
public class OAuth2ClientConfig {


    @Bean
    public OAuth2RestTemplate oauth2RestTemplate(OAuth2ProtectedResourceDetails details, OAuth2ClientContext context) {
        OAuth2RestTemplate template = new OAuth2RestTemplate(details, context);
//        AccessTokenProviderChain provider = new AccessTokenProviderChain(Arrays.asList(new ClientCredentialsAccessTokenProvider()));
//        provider.setClientTokenServices(new JdbcClientTokenServices(dataSoruce));
//        template.setAccessTokenProvider(provider);

        return template;
    }

}
