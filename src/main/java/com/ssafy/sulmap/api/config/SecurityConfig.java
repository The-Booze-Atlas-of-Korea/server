package com.ssafy.sulmap.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.sulmap.api.auth.filter.JsonObjectAuthenticationFilter;
import com.ssafy.sulmap.api.auth.service.CustomUserDetails;
import com.ssafy.sulmap.share.result.Result;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

        private final ObjectMapper objectMapper;

        /**
         * 비밀번호 암호화용 Encoder
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        /**
         * AuthenticationManager 주입용
         */
        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration authenticationConfiguration) throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
        }

        /**
         * JSON 로그인 필터 빈 등록
         */
        @Bean
        public JsonObjectAuthenticationFilter jsonObjectAuthenticationFilter(
                        AuthenticationManager authenticationManager) {
                JsonObjectAuthenticationFilter filter = new JsonObjectAuthenticationFilter(objectMapper);
                filter.setAuthenticationManager(authenticationManager);

                // 성공 핸들러: JSON 응답
                filter.setAuthenticationSuccessHandler((request, response, authentication) -> {
                        response.setContentType("application/json;charset=UTF-8");
                        response.setStatus(HttpServletResponse.SC_OK);

                        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

                        // LoginResponse DTO 생성 (여기서 직접 JSON 문자열 생성)
                        // 간단하게 Map 사용하거나 DTO를 ObjectMapper로 변환
                        Map<String, Object> responseData = new HashMap<>();
                        responseData.put("userId", userDetails.getUserId());
                        responseData.put("email", userDetails.getEmail());
                        responseData.put("name", userDetails.getName());
                        responseData.put("profileImageUrl", userDetails.getProfileImageUrl());

                        objectMapper.writeValue(response.getWriter(), Result.ok(responseData));
                });

                // 실패 핸들러: JSON 응답
                filter.setAuthenticationFailureHandler((request, response, exception) -> {
                        response.setContentType("application/json;charset=UTF-8");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        objectMapper.writeValue(response.getWriter(), Result.fail(401, "이메일 또는 비밀번호가 일치하지 않습니다."));
                });

                // SecurityContext 저장 전략 (세션 사용)
                filter.setSecurityContextRepository(new HttpSessionSecurityContextRepository());

                return filter;
        }

        /**
         * Spring Security의 필터 체인 설정
         */
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http,
                        JsonObjectAuthenticationFilter jsonObjectAuthenticationFilter) throws Exception {

                http
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/api/auth/signup",
                                                                "/api/auth/login",
                                                                "/api/public/**",
                                                                "/v3/api-docs/**",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                // 기존 formLogin 비활성화 (JSON 필터 사용)
                                .formLogin(form -> form.disable())
                                // JSON 필터를 UsernamePasswordAuthenticationFilter 위치에 추가
                                .addFilterBefore(jsonObjectAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class)

                                // 로그아웃 설정
                                .logout(logout -> logout
                                                .logoutUrl("/api/auth/logout")
                                                .deleteCookies("JSESSIONID")
                                                .logoutSuccessHandler((request, response, authentication) -> {
                                                        response.setContentType("application/json;charset=UTF-8");
                                                        response.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204
                                                }));

                return http.build();
        }
}
