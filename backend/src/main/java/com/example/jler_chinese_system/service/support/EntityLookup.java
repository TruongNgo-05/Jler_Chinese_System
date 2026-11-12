package com.example.jler_chinese_system.service.support;

import jakarta.persistence.*;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@org.springframework.stereotype.Component
public class EntityLookup {

  @PersistenceContext
  private EntityManager em;

  public <T> T find(Class<T> c, Integer id) {
    T x = id == null ? null : em.find(c, id);
    if (x == null) throw new ResponseStatusException(
      HttpStatus.NOT_FOUND,
      "Không tìm thấy dữ liệu"
    );
    return x;
  }

  public <T> List<T> all(Class<T> c) {
    return em.createQuery("from " + c.getSimpleName(), c).getResultList();
  }

  public <T> T save(T x) {
    em.persist(x);
    return x;
  }
}
