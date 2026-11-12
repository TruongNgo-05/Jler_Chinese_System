package com.example.jler_chinese_system.security;

import com.example.jler_chinese_system.entity.User;
import com.example.jler_chinese_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  @Autowired
  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @org.springframework.transaction.annotation.Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String userName)
    throws UsernameNotFoundException {
    User user = userRepository
      .findByUserName(userName)
      .orElseThrow(() ->
        new UsernameNotFoundException("User not found: " + userName)
      );
    return org.springframework.security.core.userdetails.User.builder()
      .username(user.getUserName())
      .password(user.getPassWord())
      .disabled(!Boolean.TRUE.equals(user.getStatus()))
      .roles(user.getRole().getRoleName()) // ADMIN / USER

      .build();
  }
}
