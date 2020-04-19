package com.orchid.examples.security.auth;

import lombok.Data;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
public class MyAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private String code;

    public MyAuthenticationToken(Object principal, Object credentials, String code) {
        super(principal, credentials);
        this.code=code;
    }

    public MyAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }





}
