package com.ssafy.sulmap.api.auth.controller;

import com.ssafy.sulmap.api.auth.dto.LoginRequest;
import com.ssafy.sulmap.api.auth.dto.LoginResponse;
import com.ssafy.sulmap.api.auth.service.AuthService;
import com.ssafy.sulmap.share.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Result<LoginResponse>> login(@RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        try {
            // 1. 서비스 로그인 호출 (인증 및 SecurityContext 설정)
            LoginResponse loginResponse = authService.login(request);

            // 2. 세션에 SecurityContext 저장 (명시적 처리)
            // Spring Security의 SecurityContextPersistenceFilter가 있지만,
            // 커스텀 엔드포인트에서는 명시적으로 세션에 저장하는 것이 안전함.
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());

            // 3. 성공 응답
            return ResponseEntity.ok(Result.ok(loginResponse));

        } catch (AuthenticationException e) {
            // 인증 실패 (비밀번호 불일치, 사용자 없음 등)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Result.fail(401, "이메일 또는 비밀번호가 일치하지 않습니다."));
        } catch (Exception e) {
            // 기타 서버 에러
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Result.fail(500, "로그인 처리 중 오류가 발생했습니다."));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Result<Void>> logout(HttpServletRequest request) {
        // 1. 서비스 로그아웃 (SecurityContextClear)
        authService.logout();

        // 2. 세션 무효화
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // 3. 성공 응답 (204 No Content가 일반적이나, Result 포맷 통일을 위해 200 OK + Result.ok() 사용 가능.
        // 하지만 명세에서 204를 언급했으므로 204 반환. 단, Body가 없어야 함.)
        // 명세: "204 No Content on success"
        return ResponseEntity.noContent().build();
    }
}
