package com.example.jler_chinese_system.repository;

import com.example.jler_chinese_system.entity.ComboItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ComboItemRepository extends JpaRepository<ComboItem, Integer> {
  List<ComboItem> findByCombo_ComboId(Integer comboId); // Lấy danh sách món trong combo

  @Modifying
  @Query("DELETE FROM ComboItem c WHERE c.itemId = :id")
  void deleteByItemId(@Param("id") Integer id);
}
