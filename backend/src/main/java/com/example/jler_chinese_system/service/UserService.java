package com.example.jler_chinese_system.service;

import com.example.jler_chinese_system.entity.User;

public interface UserService {
  User register(User user);

  User loginByUserName(String userName, String passWord);
}
