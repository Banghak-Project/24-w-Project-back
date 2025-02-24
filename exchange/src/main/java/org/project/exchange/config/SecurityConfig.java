package org.project.exchange.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@EnableMethodSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationEntryPoint entryPoint;

    private final String[] allowedUrls = { "/api/auth/signin", "/api/auth/signup", "/api/auth/kakao/signin",
            "/api/v1/**", "/api/auth/signup/otp", "/api/auth/signup/otp/check", "/api/v1/**",
            "/api/lists","/api/lists/**","/api/lists/add","/api/lists/delete/**","/api/lists/total/**",
            "/api/currency","/api/currency/import",
            "/api/products/add", "/api/products", "/api/products/**","/api/products/add/**"}; // 허용할 URL 목록

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // CORS 설정
                .cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource()))
                // CSRF 보호 비활성화 (REST API는 Stateless 방식이므로 CSRF 불필요)
                .csrf(CsrfConfigurer<HttpSecurity>::disable)
                // Frame 같은 Origin만 허용
                .headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin))
                // URL 인증 정책 설정
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(allowedUrls).permitAll() // 허용된 URL 목록은 인증 없이 접근 가능
                        .anyRequest().authenticated() // 나머지 모든 요청은 인증 필요
                )
                // 세션 관리 설정 (STATELESS 모드)
                .sessionManagement(
                        sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // JWT 인증 필터를 기본 인증 필터 이전에 추가
                .addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter.class)
                // 인증 실패 핸들러 설정
                .exceptionHandling(handler -> handler.authenticationEntryPoint(entryPoint))
                .build();
    }

    // CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(Arrays.asList(
                    "http://10.0.2.16", // 안드로이드 에뮬레이터용
                    "http://192.168.0.1" // 로컬 네트워크 디바이스 테스트용 (IP를 수정하여 사용)
            ));
            config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드
            config.setAllowCredentials(true); // 인증 정보 포함 허용
            config.setAllowedHeaders(Arrays.asList("*")); // 모든 요청 헤더 허용
            config.setExposedHeaders(Arrays.asList("New-Access-Token")); // 클라이언트가 접근 가능한 헤더
            config.setMaxAge(3600L); // CORS 설정 캐싱 시간 (초)
            return config;
        };
    }

    // 비밀번호 인코더 (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
