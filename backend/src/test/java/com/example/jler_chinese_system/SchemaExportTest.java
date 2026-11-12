package com.example.jler_chinese_system;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
  properties = {
    "app.initialize=false",
    "spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.jpa.properties.jakarta.persistence.schema-generation.database.action=none",
    "spring.jpa.properties.jakarta.persistence.schema-generation.scripts.action=create",
    "spring.jpa.properties.jakarta.persistence.schema-generation.scripts.create-target=target/jler_chinese_system-schema.sql",
    "spring.jpa.properties.hibernate.hbm2ddl.schema-generation.script.append=false",
  }
)
@ActiveProfiles("test")
@org.springframework.test.context.TestExecutionListeners(
  listeners = org.springframework.test.context.support.DependencyInjectionTestExecutionListener.class,
  mergeMode = org.springframework.test.context.TestExecutionListeners.MergeMode.REPLACE_DEFAULTS
)
class SchemaExportTest {

  @Test
  void exportsMySqlSchema() throws Exception {
    assertFalse(
      Files.readString(
        Path.of("target/jler_chinese_system-schema.sql")
      ).contains("foreign key (item_id)")
    );
    assertTrue(
      Files.readString(
        Path.of("target/jler_chinese_system-schema.sql")
      ).contains("create table books")
    );
  }
}
