package com.ssafy.sulmap.api.security;

import com.ssafy.sulmap.api.security.model.UserDetail;
import com.ssafy.sulmap.core.model.UserModel;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static Authentication getrefreshedAuthentication(UserModel refreshedUser) {
        UserDetail newPrincipal = new UserDetail(refreshedUser);
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

        //  새 Authentication 만들기 (principal만 교체, credentials/authorities 유지)
        return new UsernamePasswordAuthenticationToken(
                newPrincipal,
                currentAuth.getCredentials(),
                currentAuth.getAuthorities()
        );
    }
}
