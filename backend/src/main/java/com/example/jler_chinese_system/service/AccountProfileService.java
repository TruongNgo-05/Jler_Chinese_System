package com.example.jler_chinese_system.service;

import java.util.*;

public interface AccountProfileService {
  Map<String, Object> profile();

  Map<String, Object> updateProfile(Map<String, Object> d);

  void changePassword(Map<String, Object> d);
}
