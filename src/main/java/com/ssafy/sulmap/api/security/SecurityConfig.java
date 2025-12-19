package com.ssafy.sulmap.api.security;

import com.ssafy.sulmap.api.security.model.UserDetail;
import com.ssafy.sulmap.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(               // ⬅⬅⬅ 이게 포인트
        prePostEnabled = true,          // @PreAuthorize, @PostAuthorize
        securedEnabled = true,          // @Secured
        jsr250Enabled = true            // @RolesAllowed
)
public class SecurityConfig {

    private final UserService userService; // 이미 구현된 서비스


    // swagger / springdoc 경로 화이트리스트
    private static final String[] _SWAGGER_WHITELIST = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**"
    };

    /**
     * 도메인 UserService 를 사용해서 UserDetails 로 감싸주는 어댑터
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            // username = loginId
            var findUserResult = userService.findUserByLoginId(username);
            return new UserDetail(findUserResult.getOrThrow(
                    new UsernameNotFoundException("User not found: " + username)));
        };
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            DaoAuthenticationProvider authenticationProvider
    ) throws Exception {

        http
                // REST API + 세션/쿠키. CSRF는 프론트/운영 환경에 맞게 나중에 켜는 걸 추천
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 세션 기반 인증
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                // 폼 로그인, HTTP Basic 비활성화 (우리가 직접 /api/auth/login 사용)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 인가 규칙
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/login", "/auth/signup").permitAll()
                        .requestMatchers(HttpMethod.GET, "/health", "/docs/**").permitAll()
                        .requestMatchers(_SWAGGER_WHITELIST).permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated()
                )

                // 로그아웃 엔드포인트
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .deleteCookies("JSESSIONID")
                )

                // AuthenticationProvider 등록
                .authenticationProvider(authenticationProvider);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 프론트 주소를 정확히 적어야 함 (지금 에러에 나온 5173)
        config.setAllowedOrigins(List.of("http://localhost:5173"));

        // 사용하는 메소드들
        config.setAllowedMethods(List.of("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));

        // 헤더들
        config.setAllowedHeaders(List.of("*"));

        // 세션/쿠키 쓸 거면 true
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 필요한 경로만 좁히고 싶으면 "/api/**" 로
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    /**
     * 컨트롤러에서 AuthenticationManager 주입받아서 사용
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
