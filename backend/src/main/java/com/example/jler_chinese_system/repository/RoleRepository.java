package com.example.jler_chinese_system.repository;

import com.example.jler_chinese_system.entity.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
  Optional<Role> findByRoleName(String roleName); /// Lấy Role để gán quyền
}
