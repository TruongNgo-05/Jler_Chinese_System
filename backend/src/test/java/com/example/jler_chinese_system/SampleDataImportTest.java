package com.example.jler_chinese_system;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.util.*;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
  properties = {
    "app.initialize=false",
    "spring.datasource.url=jdbc:h2:mem:seed_import;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
  }
)
@ActiveProfiles("test")
@org.springframework.test.context.TestExecutionListeners(
  listeners = org.springframework.test.context.support.DependencyInjectionTestExecutionListener.class,
  mergeMode = org.springframework.test.context.TestExecutionListeners.MergeMode.REPLACE_DEFAULTS
)
class SampleDataImportTest {

  @Autowired
  DataSource source;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  com.example.jler_chinese_system.service.AuthService auth;

  private int count(java.sql.Connection c, String table) throws Exception {
    try (
      var s = c.createStatement();
      var r = s.executeQuery("select count(*) from " + table)
    ) {
      r.next();
      return r.getInt(1);
    }
  }

  @Test
  void sampleSqlImportsIsIdempotentAndPasswordsWork() throws Exception {
    try (var c = source.getConnection()) {
      var resource = script("jler_sample_data.sql");
      ScriptUtils.executeSqlScript(c, resource);
      var expected = Map.of(
        "roles",
        2,
        "users",
        3,
        "books",
        12,
        "courses",
        4,
        "lessons",
        16,
        "flashcards",
        48,
        "quizes",
        5,
        "quiz_questions",
        25,
        "quiz_options",
        100,
        "orders",
        8
      );
      for (var e : expected.entrySet())
        assertEquals(e.getValue(), count(c, e.getKey()), e.getKey());
      var before = new LinkedHashMap<String, Integer>();
      for (String table : List.of(
        "roles",
        "users",
        "books",
        "categories",
        "courses",
        "chapters",
        "lessons",
        "course_contents",
        "flashcards",
        "quizes",
        "quiz_questions",
        "quiz_options",
        "orders",
        "order_items",
        "shipping_orders",
        "user_courses",
        "carts",
        "cart_items",
        "favorite",
        "saved_word",
        "comments",
        "user_lesson_progress",
        "quiz_results",
        "quiz_attempt_details",
        "platform_post",
        "voucher"
      ))
        before.put(table, count(c, table));
      ScriptUtils.executeSqlScript(c, resource);
      for (var e : before.entrySet())
        assertEquals(
          e.getValue(),
          count(c, e.getKey()),
          "Repeated import: " + e.getKey()
        );
      try (
        var s = c.createStatement();
        var r = s.executeQuery("select role_name from roles")
      ) {
        var names = new HashSet<String>();
        while (r.next()) names.add(r.getString(1));
        assertEquals(Set.of("ADMIN", "USER"), names);
      }
      try (
        var s = c.createStatement();
        var r = s.executeQuery("select username,password from users")
      ) {
        while (r.next())
          assertTrue(
            encoder.matches(
              r.getString(1).equals("admin")
                ? "JLerAdmin2026!"
                : "JLerUser2026!",
              r.getString(2)
            )
          );
      }
      try (
        var s = c.createStatement();
        var r = s.executeQuery(
          "select count(*) from orders where total_amount<>subtotal-discount+shipping_fee"
        )
      ) {
        r.next();
        assertEquals(0, r.getInt(1));
      }
      try (
        var st = c.createStatement();
        var r = st.executeQuery(
          "select used_count from voucher where code='JLER10'"
        )
      ) {
        r.next();
        assertEquals(1, r.getInt(1));
      }
      assertEquals(0, count(c, "sepay_transactions"));
    }
    var login = new com.example.jler_chinese_system.dto.LoginRequestDTO();
    login.setUserName("demo_user1");
    login.setPassWord("JLerUser2026!");
    assertNotNull(auth.login(login).getAccessToken());
  }

  private EncodedResource script(String name) throws Exception {
    String sql = java.nio.file.Files.readString(Path.of("database", name));
    sql = sql
      .replaceAll("(?m)^CREATE DATABASE[^;]+;", "")
      .replaceAll("(?m)^USE jler_chinese_system;", "")
      .replaceAll("(?m)^SET NAMES utf8mb4;", "")
      .replace("START TRANSACTION;", "SET AUTOCOMMIT FALSE;");
    return new EncodedResource(
      new org.springframework.core.io.ByteArrayResource(
        sql.getBytes(java.nio.charset.StandardCharsets.UTF_8)
      ),
      "UTF-8"
    );
  }

  @Test
  void fullMysqlSchemaAndSeedAreStructurallyConsistent() throws Exception {
    try (
      var c = java.sql.DriverManager.getConnection(
        "jdbc:h2:mem:full_mysql_sql;MODE=MySQL;DATABASE_TO_LOWER=TRUE",
        "sa",
        ""
      )
    ) {
      ScriptUtils.executeSqlScript(c, script("jler_full_mysql.sql"));
      assertEquals(2, count(c, "roles"));
      assertEquals(12, count(c, "books"));
      assertEquals(8, count(c, "orders"));
      assertEquals(100, count(c, "quiz_options"));
    }
  }
}
