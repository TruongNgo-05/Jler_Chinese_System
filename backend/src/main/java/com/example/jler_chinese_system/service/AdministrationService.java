package com.example.jler_chinese_system.service;

import java.util.*;

public interface AdministrationService {
  List<?> adminList(String kind);

  Map<String, Object> dashboard();

  Map<String, Object> adminSave(String kind, Integer id, Map<String, Object> d);

  void adminDelete(String kind, Integer id);

  Map<String, Object> quizVisibility(Integer id, boolean active);

  Map<String, Object> cloneQuiz(Integer id);
}
