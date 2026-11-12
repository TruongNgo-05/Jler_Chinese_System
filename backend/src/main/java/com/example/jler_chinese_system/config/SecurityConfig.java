package com.example.jler_chinese_system.config;

import com.example.jler_chinese_system.security.JwtAuthenticationFilter;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtFilter;

  @Value("${app.cors-origins}")
  private String origins;

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
      .csrf(c -> c.disable())
      .cors(c -> c.configurationSource(corsConfigurationSource()))
      .sessionManagement(c ->
        c.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      )
      .exceptionHandling(c ->
        c
          .authenticationEntryPoint((q, r, e) -> {
            r.setStatus(401);
            r.setContentType("application/json;charset=UTF-8");
            r.getWriter().write("{\"message\":\"Vui lòng đăng nhập\"}");
          })
          .accessDeniedHandler((q, r, e) -> {
            r.setStatus(403);
            r.setContentType("application/json;charset=UTF-8");
            r.getWriter().write(
              "{\"message\":\"Bạn không có quyền thực hiện thao tác này\"}"
            );
          })
      )
      .authorizeHttpRequests(a ->
        a
          .dispatcherTypeMatchers(jakarta.servlet.DispatcherType.ERROR)
          .permitAll()
          .requestMatchers(HttpMethod.OPTIONS, "/**")
          .permitAll()
          .requestMatchers(HttpMethod.POST, "/api/payments/sepay/webhook")
          .permitAll()
          .requestMatchers("/api/auth/**")
          .permitAll()
          .requestMatchers(
            "/api/admin/**",
            "/api/platform/admin/**",
            "/api/images/**"
          )
          .hasRole("ADMIN")
          .requestMatchers(HttpMethod.HEAD, "/uploads/**")
          .permitAll()
          .requestMatchers(
            HttpMethod.GET,
            "/api/platform/catalog/**",
            "/api/books/**",
            "/api/courses/**",
            "/api/categories",
            "/uploads/**"
          )
          .permitAll()
          .anyRequest()
          .authenticated()
      )
      .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
      .build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration c)
    throws Exception {
    return c.getAuthenticationManager();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    var c = new CorsConfiguration();
    c.setAllowedOrigins(
      Arrays.stream(origins.split(",")).map(String::trim).toList()
    );
    c.setAllowedMethods(
      List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
    );
    c.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    var s = new UrlBasedCorsConfigurationSource();
    s.registerCorsConfiguration("/**", c);
    return s;
  }
}
