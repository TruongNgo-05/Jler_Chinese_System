package com.example.jler_chinese_system.repository;

import com.example.jler_chinese_system.entity.QuizAttemptDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizAttemptDetailRepository
  extends JpaRepository<QuizAttemptDetail, Integer> {}
