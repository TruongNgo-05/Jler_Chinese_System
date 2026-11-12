package com.example.jler_chinese_system.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  private com.example.jler_chinese_system.repository.UserRepository userRepository;

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {
    String path = request.getRequestURI();

    // ✅ 1. BỎ QUA LOGIN / REGISTER / REFRESH
    if (path.startsWith("/api/auth/")) {
      filterChain.doFilter(request, response);
      return;
    }

    // ✅ 2. LẤY HEADER
    String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      // ❗ KHÔNG TOKEN → cho qua
      filterChain.doFilter(request, response);
      return;
    }

    String token = authHeader.substring(7);
    String username;

    try {
      username = jwtUtil.extractUsername(token);
    } catch (Exception e) {
      // ❗ Token lỗi → clear context → cho qua
      SecurityContextHolder.clearContext();
      filterChain.doFilter(request, response);
      return;
    }

    // ✅ 3. SET AUTHENTICATION
    if (SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails;
      try {
        userDetails = userDetailsService.loadUserByUsername(username);
      } catch (
        org.springframework.security.core.userdetails.UsernameNotFoundException e
      ) {
        filterChain.doFilter(request, response);
        return;
      }
      var account = userRepository.findByUserName(username).orElseThrow();
      Integer version = jwtUtil
        .extractAllClaims(token)
        .get("version", Integer.class);
      if (version == null || !version.equals(account.getTokenVersion())) {
        filterChain.doFilter(request, response);
        return;
      }
      if (!userDetails.isEnabled()) {
        filterChain.doFilter(request, response);
        return;
      }

      UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
          userDetails,
          null,
          userDetails.getAuthorities()
        );

      authentication.setDetails(
        new WebAuthenticationDetailsSource().buildDetails(request)
      );

      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    filterChain.doFilter(request, response);
  }
}
