package com.ssafy.sulmap.api.auth.service;

import com.ssafy.sulmap.core.query.FindUserResult;
import com.ssafy.sulmap.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. UserRepository를 통해 유저 조회 (이메일을 loginId로 사용)
        FindUserResult userResult = userRepository.findByLoginId(username);

        // 2. 유저가 없거나, 탈퇴한 회원(deletedAt != null)인 경우 예외 발생
        if (userResult == null || userResult.getDeletedAt() != null) {
            throw new UsernameNotFoundException("User not found or deleted with email: " + username);
        }

        // 3. 비활성 상태 체크 (status 필드가 'INACTIVE' 또는 'BLOCKED' 등인 경우)
        // 현재 요구사항에서는 구체적인 status 값이 명시되지 않았으나,
        // 필요하다면 여기서 체크하여 예외를 던질 수 있습니다.
        // 예: if ("INACTIVE".equals(userResult.getStatus())) { ... }

        // 4. UserDetails 객체 생성 및 반환
        // 권한(Role)은 현재 요구사항에 없으므로 빈 리스트로 설정하거나 기본 권한 부여
        return User.builder()
                .username(userResult.getLoginId())
                .password(userResult.getPasswordHash())
                .authorities(Collections.emptyList()) // 권한 없음
                .build();
    }
}
