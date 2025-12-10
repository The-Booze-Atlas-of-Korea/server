package com.ssafy.sulmap.api.controller;

import com.ssafy.sulmap.api.dto.request.LoginRequest;
import com.ssafy.sulmap.api.dto.request.SignupRequest;
import com.ssafy.sulmap.core.model.command.CreateUserCommand;
import com.ssafy.sulmap.core.model.enums.UserGender;
import com.ssafy.sulmap.core.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager _authenticationManager;
    private final UserService _userService;

    /**
     * 로그인: loginId + password
     * 성공 시 JSESSIONID 쿠키 발급 (세션 기반)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        // 1) AuthenticationManager 에게 인증 위임
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(request.loginId(), request.password());

        Authentication authentication = _authenticationManager.authenticate(authToken);

        // 2) SecurityContext 설정
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // 3) 세션에 SecurityContext 저장 → JSESSIONID 쿠키 발급
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, context);

        // 필요한 정보만 내려주기 (토큰 X, 세션/쿠키로 인증)
        return ResponseEntity.ok().build();
    }

    /**
     * 회원가입: 이미 구현된 서비스 로직 재사용
     * 비밀번호 암호화는 UserService 내부에서 passwordEncoder 사용
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        var command = CreateUserCommand.builder()
                .loginId(request.loginId())
                .password(request.password())
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .address(request.address())
                .birthday(request.birthday())
                .gender(UserGender.fromString(request.gender()))
                .build();

        // 예시: 서비스에서 안에서 passwordEncoder.encode() 처리
        var result = _userService.CreateUser(command);

        if(result.isFailure()){
            return new ResponseEntity<>(result.getSingleErrorOrThrow().getStatus());
        }

        return ResponseEntity.ok()
                .body(new Object() {
                    public final Long userId = result.getOrThrow();
                });
    }
}
