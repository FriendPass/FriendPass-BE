package com.likelion.friendpass.config;

import com.likelion.friendpass.api.auth.JwtAuthFilter;
import com.likelion.friendpass.api.auth.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// [CHANGED] 전역 CORS를 HttpSecurity.cors(configurationSource -> ...)로 옮겨 사용
import org.springframework.web.cors.CorsConfiguration;
// [REMOVED] import org.springframework.web.cors.CorsConfigurationSource;  // 별도 Bean 제거
// [REMOVED] import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklist tokenBlacklist;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                // [CHANGED] 전역 CORS: /ws 요청은 여기서 처리하지 않도록 제외
                .cors(cors -> cors.configurationSource(request -> {
                    String uri = request.getRequestURI();
                    if (uri.startsWith("/ws")) {
                        return null; // [ADDED] /ws는 WebSocketConfig에서만 CORS 처리
                    }
                    CorsConfiguration cfg = new CorsConfiguration();
                    // [CHANGED] 허용 오리진: 프로덕션 도메인으로 정리(+개발용 로컬 유지)
                    cfg.setAllowedOriginPatterns(List.of(
                            "https://friendpass.site",
                            "https://www.friendpass.site",
                            "http://localhost:3000" // 개발용
                    ));
                    cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
                    cfg.setAllowedHeaders(List.of("Authorization","Content-Type","X-Requested-With"));
                    cfg.setAllowCredentials(true);
                    return cfg;
                }))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/nationalities/**", "/interests/**", "/schools/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthFilter(jwtTokenProvider, tokenBlacklist),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // [REMOVED] 별도 전역 CORS Bean (중복 헤더 원인 소지)
    /*
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:3000",
                "https://*.ngrok-free.app" // [REMOVED]
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    */

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
