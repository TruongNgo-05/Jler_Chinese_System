package com.example.jler_chinese_system.service;

import java.util.*;

public interface CatalogService {
  List<Map<String, Object>> catalog(String kind);

  Map<String, Object> detail(String kind, Integer id);
}
