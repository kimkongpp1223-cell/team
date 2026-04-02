package com.sosial.damoa.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .addFilterBefore(new AdminTokenFilter(), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    static class AdminTokenFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {

            String uri = request.getRequestURI();

            // 관리자 로그인은 통과
            if ("/api/admin/login".equals(uri)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 관리자 API만 토큰 검사
            if (uri.startsWith("/api/admin/")) {
                String token = request.getHeader("Authorization");

                if (token == null || !token.equals("admin-token")) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("text/plain; charset=UTF-8");
                    response.getWriter().write("Unauthorized");
                    return;
                }
            }

            filterChain.doFilter(request, response);
        }
    }
}