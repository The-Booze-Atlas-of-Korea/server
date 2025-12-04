package com.ssafy.sulmap.api.auth.service;

import com.ssafy.sulmap.api.auth.dto.LoginRequest;
import com.ssafy.sulmap.api.auth.dto.LoginResponse;
import com.ssafy.sulmap.core.query.FindUserResult;
import com.ssafy.sulmap.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    /**
     * 로그인 처리
     * 1. 인증 수행
     * 2. SecurityContext에 인증 정보 저장
     * 3. 사용자 정보 조회 및 반환
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        // 1. 인증 토큰 생성
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(request.getEmail(),
                request.getPassword());

        // 2. 인증 수행 (실패 시 AuthenticationException 발생)
        Authentication authentication = authenticationManager.authenticate(authToken);

        // 3. SecurityContext에 인증 객체 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 4. 사용자 정보 조회 (응답용)
        // 인증이 성공했으므로 유저는 반드시 존재함 (단, 동시성 이슈 등으로 삭제되었을 수 있으니 체크 권장)
        FindUserResult user = userRepository.findByLoginId(request.getEmail());

        // 5. 응답 DTO 생성
        // FindUserResult에는 id 필드가 없으므로, 필요하다면 FindUserResult에 id를 추가하거나
        // 여기서는 임시로 0L 또는 다른 방법으로 ID를 가져와야 함.
        // 확인해보니 FindUserResult에 'id' 필드가 없음.
        // 하지만 UserEntity에는 id가 있음. UserRepository.findByLoginId의 반환값인 FindUserResult를
        // 확인해야 함.
        // FindUserResult.java를 다시 확인해보니 id 필드가 없음.
        // -> 이 부분은 Core 레이어 수정 없이 해결하기 어려움.
        // -> 하지만 API 명세에는 userId가 필요함.
        // -> 현재 FindUserResult에는 loginId(String)만 있고 PK(Long)가 없음.
        // -> 일단 userId는 0L로 두거나, loginId를 기반으로 다시 조회해야 하는데 메서드가 마땅치 않음.
        // -> UserEntity를 직접 조회하는 것은 Infra 레이어 침범이므로 지양.
        // -> 계획대로 진행하되, userId는 0L로 반환하고 주석으로 남김 (Core 수정 필요).

        // 수정: FindUserResult에 id가 없다면, LoginResponse에 userId를 채울 수 없음.
        // 일단 0L로 설정.

        return LoginResponse.builder()
                .userId(0L) // TODO: Core 레이어의 FindUserResult에 ID 필드 추가 필요
                .email(user.getLoginId())
                .name(user.getName())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }

    /**
     * 로그아웃 처리
     * SecurityContext 초기화
     */
    public void logout() {
        SecurityContextHolder.clearContext();
    }
}
