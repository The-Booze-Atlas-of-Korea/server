package com.ssafy.sulmap.api.auth.service;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUserDetails extends User {

    private final Long userId;
    private final String email;
    private final String name;
    private final String profileImageUrl;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities,
            Long userId, String email, String name, String profileImageUrl) {
        super(username, password, authorities);
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }
}
