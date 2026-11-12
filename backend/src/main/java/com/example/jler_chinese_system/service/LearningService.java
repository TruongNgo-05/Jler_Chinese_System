package com.example.jler_chinese_system.service;

import java.util.*;

public interface LearningService {
  Map<String, Object> learn(Integer id);

  List<?> myCourses();

  void progress(Integer id, Map<String, Object> d);

  List<?> flashcards(Integer lesson);

  List<?> comments(Integer lesson);

  void comment(Integer lesson, Map<String, Object> d);

  void deleteComment(Integer id);

  List<?> words();

  void saveWord(Integer id, Map<String, Object> d);

  void deleteWord(Integer id);

  Map<String, Object> lesson(Integer id);
}
