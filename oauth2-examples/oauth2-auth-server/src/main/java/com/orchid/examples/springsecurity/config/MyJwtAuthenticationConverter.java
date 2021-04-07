package com.orchid.examples.springsecurity.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;
import java.util.stream.Collectors;

public class MyJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {



    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        List<String> authoritys=jwt.getClaimAsStringList("authorities");
        List<SimpleGrantedAuthority> simpleGrantedAuthorityList=authoritys.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
//        Collection<GrantedAuthority> authorities = this.extractAuthorities(jwt);
        return new JwtAuthenticationToken(jwt, simpleGrantedAuthorityList);
    }

}
