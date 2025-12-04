package com.ssafy.sulmap.api.auth.service;

import com.ssafy.sulmap.core.query.FindUserResult;
import com.ssafy.sulmap.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

        // 4. CustomUserDetails 객체 생성 및 반환
        // FindUserResult에 userId(PK)가 없으므로 0L로 설정 (Core 수정 필요)
        return new CustomUserDetails(
                userResult.getLoginId(),
                userResult.getPasswordHash(),
                Collections.emptyList(),
                0L, // TODO: Core 레이어 수정 후 실제 ID 매핑 필요
                userResult.getEmail(),
                userResult.getName(),
                userResult.getProfileImageUrl());
    }
}
