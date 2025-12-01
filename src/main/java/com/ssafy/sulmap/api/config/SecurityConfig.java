package com.ssafy.sulmap.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(               // ⬅⬅⬅ 이게 포인트
        prePostEnabled = true,          // @PreAuthorize, @PostAuthorize
        securedEnabled = true,          // @Secured
        jsr250Enabled = true            // @RolesAllowed
)
public class SecurityConfig {

    /**
     * 비밀번호 암호화용 Encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager 주입용 (로그인 필터 등이 필요할 때 사용)
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Spring Security의 필터 체인 설정
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // REST 예제에서 간단히 CSRF 비활성화 (실서비스에서는 꼭 전략 고민!)
                .csrf(csrf -> csrf.disable())

                // 세션 설정: 필요할 때만 세션 생성, JSESSIONID 쿠키로 클라이언트와 연결
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                // URL 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/signup",
                                "/api/auth/login",
                                "/api/public/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // 로그인 설정: /api/auth/login 으로 POST 시 UsernamePasswordAuthenticationFilter가 동작
                .formLogin(form -> form
                        .loginProcessingUrl("/api/auth/login")
                        // loginPage 지정 안하면 기본 로그인 폼 사용 (REST에서는 직접 JSON 로그인 필터 만들어도 됨)
                        .successHandler((request, response, authentication) -> {
                            // 로그인 성공 시 JSON 반환 (세션 + JSESSIONID 쿠키는 자동 설정)
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("""
                                    {"result":"ok","message":"login success"}
                                    """);
                        })
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(401);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("""
                                    {"result":"fail","message":"invalid credentials"}
                                    """);
                        })
                        .permitAll()
                )

                // 로그아웃: 세션 무효화 + JSESSIONID 쿠키 삭제
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("""
                                    {"result":"ok","message":"logout success"}
                                    """);
                        })
                );
        return http.build();
    }
}
