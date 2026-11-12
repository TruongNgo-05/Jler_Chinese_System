package com.example.jler_chinese_system.service.impl;

import com.example.jler_chinese_system.entity.Role;
import com.example.jler_chinese_system.entity.User;
import com.example.jler_chinese_system.repository.RoleRepository;
import com.example.jler_chinese_system.repository.UserRepository;
import com.example.jler_chinese_system.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@org.springframework.transaction.annotation.Transactional
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userepository;

  @Autowired
  private RoleRepository rolerepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public User register(User user) {
    user.setEmail(user.getEmail().trim().toLowerCase(java.util.Locale.ROOT));
    user.setUserName(user.getUserName().trim());
    if (userepository.existsByEmail(user.getEmail())) {
      throw new ResponseStatusException(
        HttpStatus.CONFLICT,
        "Email đã được đăng ký. Hãy dùng email khác hoặc đăng nhập."
      );
    }
    if (userepository.existsByUserName(user.getUserName())) {
      throw new ResponseStatusException(
        HttpStatus.CONFLICT,
        "Tên đăng nhập đã tồn tại. Hãy chọn tên khác."
      );
    }
    Role userRole = rolerepository
      .findByRoleName("USER")
      .orElseThrow(() ->
        new ResponseStatusException(
          HttpStatus.SERVICE_UNAVAILABLE,
          "Chưa khởi tạo vai trò học viên. Hãy khởi động lại backend."
        )
      );

    if (
      user
        .getPassWord()
        .getBytes(java.nio.charset.StandardCharsets.UTF_8)
        .length > 72
    ) throw new ResponseStatusException(
      HttpStatus.BAD_REQUEST,
      "Mật khẩu quá dài: tối đa 72 byte UTF-8"
    );
    user.setRole(userRole);
    user.setStatus(true);
    user.setCreatedAt(java.time.LocalDateTime.now());

    user.setPassWord(passwordEncoder.encode(user.getPassWord()));
    return userepository.save(user);
  }

  @Override
  public User loginByUserName(String userName, String passWord) {
    User user = userepository
      .findByUserName(userName)
      .orElseThrow(() -> new RuntimeException("Tài khoản  không tồn tại"));

    if (!passwordEncoder.matches(passWord, user.getPassWord())) {
      throw new ResponseStatusException(
        HttpStatus.UNAUTHORIZED,
        "Sai mật khẩu"
      );
    }
    return user;
  }
}
