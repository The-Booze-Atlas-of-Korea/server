package com.ssafy.sulmap.api.dto;

import com.ssafy.sulmap.core.model.UserModel;
import com.ssafy.sulmap.core.model.enums.UserStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@RequiredArgsConstructor
public class LoginUserDetail implements UserDetails {
    private final UserModel userModel;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한 시스템 사용 시 여기에 매핑

        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return userModel.getPasswordHash();
    }

    @Override
    public String getUsername() {
        // 로그인 ID
        return userModel.getLoginId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return userModel.getStatus() == UserStatus.ACTIVE;
    }

    @Override
    public boolean isAccountNonLocked() {
        return userModel.getStatus() != UserStatus.BANNED; // 잠금 정책 있다면 여기서 처리
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        // 예: UserStatus.ACTIVE 일 때만 true
        return userModel.getStatus() == null || userModel.getStatus() == UserStatus.ACTIVE;
    }
}
