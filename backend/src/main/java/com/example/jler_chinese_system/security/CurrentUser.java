package com.example.jler_chinese_system.security;

import com.example.jler_chinese_system.constant.RoleNames;
import com.example.jler_chinese_system.entity.User;
import com.example.jler_chinese_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

@org.springframework.stereotype.Component
@RequiredArgsConstructor
public class CurrentUser {

  private final UserRepository users;

  public User get() {
    var a = SecurityContextHolder.getContext().getAuthentication();
    if (a == null) throw new ResponseStatusException(
      HttpStatus.UNAUTHORIZED,
      "Vui lòng đăng nhập"
    );
    return users
      .findByUserName(a.getName())
      .orElseThrow(() ->
        new ResponseStatusException(
          HttpStatus.UNAUTHORIZED,
          "Vui lòng đăng nhập"
        )
      );
  }

  public boolean isAdmin() {
    return RoleNames.ADMIN.equals(get().getRole().getRoleName());
  }
}
