package com.example.jler_chinese_system.service.impl;

import static com.example.jler_chinese_system.common.Checks.check;
import static com.example.jler_chinese_system.common.ResponseMaps.map;
import static com.example.jler_chinese_system.mapper.PlatformMapper.*;
import static com.example.jler_chinese_system.utils.RequestValues.*;

import com.example.jler_chinese_system.entity.*;
import com.example.jler_chinese_system.mapper.PlatformMapper;
import com.example.jler_chinese_system.repository.UserRepository;
import com.example.jler_chinese_system.security.CurrentUser;
import com.example.jler_chinese_system.service.*;
import com.example.jler_chinese_system.service.support.*;
import jakarta.persistence.*;
import java.math.*;
import java.time.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountProfileServiceImpl implements AccountProfileService {

  @PersistenceContext
  private EntityManager em;

  private final CurrentUser currentUser;
  private final org.springframework.security.crypto.password.PasswordEncoder encoder;

  public Map<String, Object> profile() {
    return userView(currentUser.get());
  }

  public Map<String, Object> updateProfile(Map<String, Object> d) {
    User u = currentUser.get();
    u.setFullName(required(d, "fullName"));
    u.setPhone(text(d, "phone"));
    u.setAddress(text(d, "address"));
    u.setAvatarrUrl(url(d, "avatar"));
    return userView(u);
  }

  public void changePassword(Map<String, Object> d) {
    User u = currentUser.get();
    check(
      encoder.matches(required(d, "oldPassword"), u.getPassWord()),
      "Mật khẩu hiện tại không đúng"
    );
    String p = required(d, "newPassword");
    check(
      p.length() >= 8 && p.length() <= 72,
      "Mật khẩu phải từ 8 đến 72 ký tự"
    );
    check(p.equals(text(d, "confirmPassword")), "Xác nhận mật khẩu không khớp");
    u.setPassWord(encoder.encode(p));
    u.setTokenVersion(u.getTokenVersion() + 1);
    em.createQuery("delete from RefreshToken t where t.user=:u")
      .setParameter("u", u)
      .executeUpdate();
  }
}
