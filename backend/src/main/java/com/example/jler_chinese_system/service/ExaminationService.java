package com.example.jler_chinese_system.service;

import java.util.*;

public interface ExaminationService {
  Map<String, Object> startQuiz(Integer id);

  Map<String, Object> submitQuiz(Integer session, Map<String, Object> d);

  List<?> results();

  Map<String, Object> result(Integer id);
}
