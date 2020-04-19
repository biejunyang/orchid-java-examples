package com.orchid.examples.security.service;

import cn.hutool.core.collection.CollectionUtil;
import com.orchid.examples.security.auth.MyAuthenticationUser;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.UserDetailsManagerConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        if("admin".equals(s)){
            MyAuthenticationUser user=new MyAuthenticationUser();
            user.setUsername("admin");
            user.setPassword("{noop}123456");
            user.setAuthorities(CollectionUtil.newArrayList(
                    new SimpleGrantedAuthority("ADMIN"),
                    new SimpleGrantedAuthority("USER")));
            return user;
        }
        return null;
    }
}
