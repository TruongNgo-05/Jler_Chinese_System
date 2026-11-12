package com.example.jler_chinese_system;

import static org.junit.jupiter.api.Assertions.*;

import java.net.*;
import java.net.http.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.databind.*;

@org.springframework.context.annotation.Import(
  JlerChineseSystemApplicationTests.TestMailConfig.class
)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@org.springframework.test.context.TestExecutionListeners(
  listeners = org.springframework.test.context.support.DependencyInjectionTestExecutionListener.class,
  mergeMode = org.springframework.test.context.TestExecutionListeners.MergeMode.REPLACE_DEFAULTS
)
class JlerChineseSystemApplicationTests {

  @LocalServerPort
  int port;

  final ObjectMapper json = new ObjectMapper();
  final HttpClient http = HttpClient.newHttpClient();
  String admin;
  int book, course, quiz, paidLesson;

  record Reply(int status, JsonNode body) {}

  Reply req(String method, String path, String token, Object body)
    throws Exception {
    var b = HttpRequest.newBuilder(
      URI.create("http://localhost:" + port + "/api" + path)
    );
    if (token != null) b.header("Authorization", "Bearer " + token);
    if (body != null) b.header("Content-Type", "application/json");
    b.method(
      method,
      body == null
        ? HttpRequest.BodyPublishers.noBody()
        : HttpRequest.BodyPublishers.ofString(json.writeValueAsString(body))
    );
    var r = http.send(b.build(), HttpResponse.BodyHandlers.ofString());
    return new Reply(
      r.statusCode(),
      r.body().isBlank() ? json.createObjectNode() : json.readTree(r.body())
    );
  }

  Reply get(String path, String token) throws Exception {
    return req("GET", path, token, null);
  }

  String user() throws Exception {
    String name = "u" + UUID.randomUUID().toString().replace("-", "");
    assertEquals(
      200,
      req(
        "POST",
        "/auth/register",
        null,
        Map.of(
          "userName",
          name,
          "passWord",
          "Student123!",
          "confirmPassword",
          "Student123!",
          "email",
          name + "@test.local",
          "fullName",
          "Học viên",
          "phone",
          "0912345678"
        )
      ).status
    );
    return login(name, "Student123!");
  }

  String login(String name, String pass) throws Exception {
    var r = req(
      "POST",
      "/auth/login",
      null,
      Map.of("userName", name, "passWord", pass)
    );
    assertEquals(200, r.status, r.body.toString());
    return r.body.get("accessToken").asText();
  }

  void add(String u, String type, int id) throws Exception {
    assertEquals(
      200,
      req(
        "POST",
        "/platform/cart",
        u,
        Map.of("itemType", type, "itemId", id)
      ).status
    );
  }

  Reply buy(String u) throws Exception {
    return req(
      "POST",
      "/platform/orders",
      u,
      Map.of(
        "paymentMethod",
        "BANK_TRANSFER",
        "fullName",
        "Người nhận",
        "phone",
        "0912345678",
        "address",
        "Hà Nội"
      )
    );
  }

  @Test
  void cartAdjustPersistsAndChecksOwnership() throws Exception {
    String u = user();
    add(u, "book", book);
    var cart = get("/platform/cart", u).body;
    int id = cart.get("items").get(0).get("id").asInt();
    for (int quantity = 2; quantity <= 4; quantity++) {
      var changed = req(
        "POST",
        "/platform/cart/" + id + "/adjust",
        u,
        Map.of("delta", 1)
      );
      assertEquals(200, changed.status, changed.body.toString());
      assertEquals(
        quantity,
        changed.body.get("items").get(0).get("quantity").asInt()
      );
      assertEquals(
        quantity,
        get("/platform/cart", u)
          .body.get("items")
          .get(0)
          .get("quantity")
          .asInt()
      );
    }
    assertEquals(
      404,
      req(
        "POST",
        "/platform/cart/" + id + "/adjust",
        user(),
        Map.of("delta", 1)
      ).status
    );
    assertEquals(
      400,
      req(
        "POST",
        "/platform/cart/" + id + "/adjust",
        u,
        Map.of("delta", 5)
      ).status
    );
    assertEquals(
      200,
      req(
        "POST",
        "/platform/cart/" + id + "/adjust",
        u,
        Map.of("delta", -1)
      ).status
    );
  }

  @Test
  void checkoutRetryIsIdempotentAndRejectsChangedPayload() throws Exception {
    String u = user();
    add(u, "Book", book);
    var payload = new HashMap<String, Object>(
      Map.of(
        "paymentMethod",
        "BANK_TRANSFER",
        "fullName",
        "Test",
        "phone",
        "0912345678",
        "address",
        "Hanoi",
        "checkoutKey",
        UUID.randomUUID().toString()
      )
    );
    var first = req("POST", "/platform/orders", u, payload);
    assertEquals(200, first.status, first.body.toString());
    var retry = req("POST", "/platform/orders", u, payload);
    assertEquals(200, retry.status, retry.body.toString());
    assertEquals(first.body.get("id"), retry.body.get("id"));
    payload.put("address", "Other");
    assertEquals(409, req("POST", "/platform/orders", u, payload).status);
    assertEquals(1, get("/platform/orders", u).body.size());
  }

  @Test
  void courseQuantityIsOneAndCannotUseFreeForPaidCourse() throws Exception {
    String u = user();
    add(u, "course", course);
    add(u, "Course", course);
    var item = get("/platform/cart", u).body.get("items").get(0);
    assertEquals(1, item.get("quantity").asInt());
    assertEquals(
      400,
      req(
        "POST",
        "/platform/cart/" + item.get("id").asInt() + "/adjust",
        u,
        Map.of("delta", 1)
      ).status
    );
    assertEquals(
      400,
      req("POST", "/platform/orders", u, Map.of("paymentMethod", "FREE")).status
    );
  }

  @Test
  void reportsAndQuizArchiveCloneAreAdminOnly() throws Exception {
    assertEquals(403, get("/platform/admin/reports", user()).status);
    var report = get("/platform/admin/reports", admin);
    assertEquals(200, report.status, report.body.toString());
    assertEquals(30, report.body.get("daily").size());
    assertEquals(
      400,
      get("/platform/admin/reports?from=invalid", admin).status
    );
    var clone = req(
      "POST",
      "/platform/admin/quizzes/" + quiz + "/duplicate",
      admin,
      null
    );
    assertEquals(200, clone.status, clone.body.toString());
    int id = clone.body.get("id").asInt();
    assertFalse(clone.body.get("active").asBoolean());
    assertTrue(clone.body.get("questionCount").asInt() > 0);
    var visible = req(
      "PUT",
      "/platform/admin/quizzes/" + id + "/visibility",
      admin,
      Map.of("active", true)
    );
    assertEquals(200, visible.status, visible.body.toString());
    assertTrue(visible.body.get("active").asBoolean());
    req(
      "PUT",
      "/platform/admin/quizzes/" + id + "/visibility",
      admin,
      Map.of("active", false)
    );
    for (var row : get("/platform/catalog/quizzes", null).body)
      assertNotEquals(id, row.get("id").asInt());
  }

  @Test
  void publicMediaSupportsHeadAndVideoByteRanges() throws Exception {
    for (String name : List.of(
      "study-desk.png",
      "hsk-books.png",
      "conversation.png"
    )) {
      var response = http.send(
        HttpRequest.newBuilder(
          URI.create("http://localhost:" + port + "/uploads/jler-media/" + name)
        )
          .method("HEAD", HttpRequest.BodyPublishers.noBody())
          .build(),
        HttpResponse.BodyHandlers.discarding()
      );
      assertEquals(200, response.statusCode(), name);
      assertTrue(
        response
          .headers()
          .firstValue("Content-Type")
          .orElse("")
          .startsWith("image/png")
      );
    }
    for (String name : List.of("chao-hoi.mp4", "so-dem.mp4", "giao-tiep.mp4")) {
      var response = http.send(
        HttpRequest.newBuilder(
          URI.create("http://localhost:" + port + "/uploads/jler-media/" + name)
        )
          .header("Range", "bytes=0-1023")
          .GET()
          .build(),
        HttpResponse.BodyHandlers.ofByteArray()
      );
      assertEquals(206, response.statusCode(), name);
      assertEquals(1024, response.body().length);
      assertTrue(
        response
          .headers()
          .firstValue("Content-Type")
          .orElse("")
          .startsWith("video/mp4")
      );
      assertTrue(
        response
          .headers()
          .firstValue("Content-Range")
          .orElse("")
          .startsWith("bytes 0-1023/")
      );
    }
  }

  @BeforeAll
  void setup() throws Exception {
    admin = login("admin", "TestAdmin2026!Secure");
    book = get("/platform/catalog/books", null).body.get(0).get("id").asInt();
    course = get("/platform/catalog/courses", null)
      .body.get(0)
      .get("id")
      .asInt();
    quiz = get("/platform/catalog/quizzes", null).body.get(0).get("id").asInt();
    var lessons = get("/platform/admin/lessons", admin).body;
    for (var l : lessons)
      if (!l.get("isFree").asBoolean()) {
        paidLesson = l.get("id").asInt();
        break;
      }
  }

  @Test
  void publicCatalogAndProtectedAdmin() throws Exception {
    assertEquals(200, get("/platform/catalog/books", null).status);
    assertEquals(401, get("/platform/admin/users", null).status);
    assertEquals(403, get("/platform/admin/users", user()).status);
    assertEquals(401, get("/platform/cart", null).status);
    assertEquals(200, get("/platform/admin/users", admin).status);
  }

  @Test
  void malformedJwtDoesNotAuthenticate() throws Exception {
    assertEquals(401, get("/platform/me", "bad.token").status);
  }

  @Test
  void registrationCannotChooseRole() throws Exception {
    var u = user();
    assertEquals("USER", get("/platform/me", u).body.get("role").asText());
    assertFalse(get("/platform/me", u).body.has("password"));
  }

  @Test
  void rejectsWrongPassword() throws Exception {
    assertEquals(
      401,
      req(
        "POST",
        "/auth/login",
        null,
        Map.of("userName", "admin", "passWord", "incorrect")
      ).status
    );
  }

  @Test
  void emptyCartAndCheckoutValidation() throws Exception {
    var u = user();
    assertEquals(0, get("/platform/cart", u).body.get("items").size());
    assertEquals(400, buy(u).status);
    assertEquals(
      400,
      req(
        "POST",
        "/platform/cart",
        u,
        Map.of("itemId", book, "itemType", "Unknown")
      ).status
    );
  }

  @Test
  void cartItemBelongsToOwner() throws Exception {
    var a = user();
    var b = user();
    add(a, "Book", book);
    int id = get("/platform/cart", a)
      .body.get("items")
      .get(0)
      .get("id")
      .asInt();
    assertEquals(
      404,
      req("PUT", "/platform/cart/" + id, b, Map.of("quantity", 2)).status
    );
    assertEquals(404, req("DELETE", "/platform/cart/" + id, b, null).status);
    assertEquals(
      1,
      get("/platform/cart", a).body.get("items").get(0).get("quantity").asInt()
    );
  }

  @Test
  void negativeQuantityAndExcessStockRejected() throws Exception {
    var u = user();
    assertEquals(
      400,
      req(
        "POST",
        "/platform/cart",
        u,
        Map.of("itemId", book, "itemType", "Book", "quantity", 999)
      ).status
    );
    add(u, "Book", book);
    int id = get("/platform/cart", u)
      .body.get("items")
      .get(0)
      .get("id")
      .asInt();
    assertEquals(
      400,
      req("PUT", "/platform/cart/" + id, u, Map.of("quantity", -1)).status
    );
  }

  @Test
  void checkoutReservesStockAndCancelRestoresOnce() throws Exception {
    var u = user();
    int before = get("/platform/catalog/books/" + book, null)
      .body.get("stock")
      .asInt();
    add(u, "Book", book);
    var o = buy(u);
    assertEquals(200, o.status, o.body.toString());
    int id = o.body.get("id").asInt();
    assertEquals(
      before - 1,
      get("/platform/catalog/books/" + book, null)
        .body.get("stock")
        .asInt()
    );
    assertEquals(0, get("/platform/cart", u).body.get("items").size());
    assertEquals(
      400,
      req(
        "POST",
        "/platform/orders/" + id + "/actions",
        user(),
        Map.of("action", "CANCEL")
      ).status
    );
    assertEquals(
      200,
      req(
        "POST",
        "/platform/orders/" + id + "/actions",
        u,
        Map.of("action", "CANCEL")
      ).status
    );
    assertEquals(
      before,
      get("/platform/catalog/books/" + book, null)
        .body.get("stock")
        .asInt()
    );
    assertEquals(
      400,
      req(
        "POST",
        "/platform/orders/" + id + "/actions",
        u,
        Map.of("action", "CANCEL")
      ).status
    );
  }

  @Test
  void courseRequiresPaymentAndGrantIsIdempotent() throws Exception {
    var u = user();
    assertEquals(400, get("/platform/learn/" + course, u).status);
    add(u, "Course", course);
    add(u, "Course", course);
    assertEquals(
      1,
      get("/platform/cart", u).body.get("items").get(0).get("quantity").asInt()
    );
    var order = buy(u);
    assertEquals(200, order.status, order.body.toString());
    int id = order.body.get("id").asInt();
    assertEquals(0, get("/platform/my-courses", u).body.size());
    assertEquals(
      200,
      req(
        "POST",
        "/platform/admin/orders/" + id + "/actions",
        admin,
        Map.of("action", "MARK_PAID")
      ).status
    );
    assertEquals(1, get("/platform/my-courses", u).body.size());
    assertEquals(200, get("/platform/learn/" + course, u).status);
    assertEquals(
      400,
      req(
        "POST",
        "/platform/admin/orders/" + id + "/actions",
        admin,
        Map.of("action", "MARK_PAID")
      ).status
    );
    assertEquals(
      400,
      req(
        "POST",
        "/platform/orders/" + id + "/actions",
        u,
        Map.of("action", "CANCEL")
      ).status
    );
    assertEquals(
      400,
      req(
        "POST",
        "/platform/cart",
        u,
        Map.of("itemId", course, "itemType", "Course")
      ).status
    );
  }

  @Test
  void protectedLessonAndCatalogDoNotLeakContent() throws Exception {
    assertEquals(
      400,
      get("/platform/lessons/" + paidLesson + "/flashcards", user()).status
    );
    var c = get("/platform/catalog/courses/" + course, null).body;
    for (var ch : c.get("chapters"))
      for (var content : ch.get("contents")) {
        var l = content.get("lesson");
        if (l != null && !l.isNull() && !l.get("isFree").asBoolean()) {
          assertTrue(l.get("videoUrl").isNull());
          assertTrue(l.get("content").isNull());
        }
      }
  }

  @Test
  void voucherComputedOnServer() throws Exception {
    var u = user();
    add(u, "Book", book);
    var q = get("/platform/cart/quote?code=JLER10", u);
    assertEquals(200, q.status);
    assertTrue(q.body.get("discount").asDouble() > 0);
    assertEquals(400, get("/platform/cart/quote?code=BAD", u).status);
  }

  @Test
  void favoritesTogglePersists() throws Exception {
    var u = user();
    var body = Map.of("itemId", book, "itemType", "Book");
    assertEquals(200, req("POST", "/platform/favorites", u, body).status);
    assertEquals(1, get("/platform/favorites", u).body.size());
    req("POST", "/platform/favorites", u, body);
    assertEquals(0, get("/platform/favorites", u).body.size());
  }

  @Test
  void quizHidesAnswersGradesAndRejectsReplay() throws Exception {
    var u = user();
    var start = req("POST", "/platform/quizzes/" + quiz + "/start", u, null);
    assertEquals(200, start.status, start.body.toString());
    int session = start.body.get("sessionId").asInt();
    var questions = start.body.get("questions");
    assertFalse(questions.get(0).get("options").get(0).has("correct"));
    var resumed = req("POST", "/platform/quizzes/" + quiz + "/start", u, null);
    assertEquals(session, resumed.body.get("sessionId").asInt());
    var answers = new LinkedHashMap<String, Integer>();
    for (var q : questions)
      answers.put(
        q.get("id").asText(),
        q.get("options").get(0).get("id").asInt()
      );
    assertEquals(
      400,
      req(
        "POST",
        "/platform/quiz-sessions/" + session + "/submit",
        user(),
        Map.of("answers", answers)
      ).status
    );
    var r = req(
      "POST",
      "/platform/quiz-sessions/" + session + "/submit",
      u,
      Map.of("answers", answers)
    );
    assertEquals(200, r.status, r.body.toString());
    assertEquals(100, r.body.get("score").asInt());
    assertEquals(
      400,
      req(
        "POST",
        "/platform/quiz-sessions/" + session + "/submit",
        u,
        Map.of("answers", answers)
      ).status
    );
    assertEquals(
      400,
      get("/platform/results/" + r.body.get("id").asInt(), user()).status
    );
  }

  @Test
  void quizRejectsForeignOption() throws Exception {
    var u = user();
    var s = req("POST", "/platform/quizzes/" + quiz + "/start", u, null).body;
    var qs = s.get("questions");
    assertEquals(
      400,
      req(
        "POST",
        "/platform/quiz-sessions/" + s.get("sessionId").asInt() + "/submit",
        u,
        Map.of(
          "answers",
          Map.of(
            qs.get(0).get("id").asText(),
            qs.get(1).get("options").get(0).get("id").asInt()
          )
        )
      ).status
    );
  }

  @Test
  void profileCannotEscalateRole() throws Exception {
    var u = user();
    assertEquals(
      200,
      req(
        "PUT",
        "/platform/me",
        u,
        Map.of(
          "fullName",
          "Tên mới",
          "role",
          "ADMIN",
          "avatar",
          "",
          "phone",
          "0912345678",
          "address",
          "HN"
        )
      ).status
    );
    assertEquals("USER", get("/platform/me", u).body.get("role").asText());
  }

  @Test
  void passwordValidation() throws Exception {
    var u = user();
    assertEquals(
      400,
      req(
        "PUT",
        "/platform/me/password",
        u,
        Map.of(
          "oldPassword",
          "bad",
          "newPassword",
          "NewStudent123!",
          "confirmPassword",
          "NewStudent123!"
        )
      ).status
    );
    assertEquals(
      200,
      req(
        "PUT",
        "/platform/me/password",
        u,
        Map.of(
          "oldPassword",
          "Student123!",
          "newPassword",
          "NewStudent123!",
          "confirmPassword",
          "NewStudent123!"
        )
      ).status
    );
    assertEquals(401, get("/platform/me", u).status);
  }

  @Test
  void invalidResetAndAdminSelfLockRejected() throws Exception {
    assertEquals(
      400,
      req(
        "POST",
        "/auth/reset-password",
        null,
        Map.of(
          "token",
          "bad",
          "password",
          "NewStudent123!",
          "confirmPassword",
          "NewStudent123!"
        )
      ).status
    );
    var me = get("/platform/me", admin).body;
    assertEquals(
      400,
      req(
        "PUT",
        "/platform/admin/users/" + me.get("id").asInt(),
        admin,
        Map.of(
          "fullName",
          "Admin",
          "email",
          "admin@jler.local",
          "role",
          "ADMIN",
          "active",
          false
        )
      ).status
    );
  }

  @org.springframework.beans.factory.annotation.Autowired
  jakarta.persistence.EntityManagerFactory emf;

  @org.springframework.beans.factory.annotation.Autowired
  org.springframework.transaction.PlatformTransactionManager transactions;

  static final java.util.concurrent.ConcurrentLinkedQueue<org.springframework.mail.SimpleMailMessage> sent =
    new java.util.concurrent.ConcurrentLinkedQueue<>();

  @org.springframework.boot.test.context.TestConfiguration
  static class TestMailConfig {

    @org.springframework.context.annotation.Bean
    @org.springframework.context.annotation.Primary
    org.springframework.mail.javamail.JavaMailSender testSender() {
      return new org.springframework.mail.javamail.JavaMailSenderImpl() {
        @Override
        public void send(org.springframework.mail.SimpleMailMessage message) {
          sent.add(message);
        }
      };
    }
  }

  @Test
  void personalVocabularyOwnership() throws Exception {
    var a = user();
    var b = user();
    assertEquals(
      200,
      req(
        "POST",
        "/platform/words",
        a,
        Map.of("front", "你好", "back", "Xin chào")
      ).status
    );
    var word = get("/platform/words", a).body.get(0);
    int id = word.get("id").asInt();
    assertEquals(400, req("DELETE", "/platform/words/" + id, b, null).status);
    assertEquals(
      200,
      req(
        "PUT",
        "/platform/words/" + id,
        a,
        Map.of("front", "你好", "back", "Xin chào", "mastered", true)
      ).status
    );
    assertTrue(
      get("/platform/words", a).body.get(0).get("mastered").asBoolean()
    );
  }

  @Test
  void lastCopyCannotBeSoldTwice() throws Exception {
    var created = req(
      "POST",
      "/platform/admin/books",
      admin,
      Map.of(
        "title",
        "Last copy " + UUID.randomUUID(),
        "description",
        "Test",
        "price",
        100000,
        "stock",
        1,
        "categoryIds",
        List.of(1),
        "imageUrl",
        ""
      )
    ).body;
    int id = created.get("id").asInt();
    var a = user();
    var b = user();
    add(a, "Book", id);
    add(b, "Book", id);
    var one = java.util.concurrent.CompletableFuture.supplyAsync(() -> {
      try {
        return buy(a).status;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
    var two = java.util.concurrent.CompletableFuture.supplyAsync(() -> {
      try {
        return buy(b).status;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
    var statuses = new ArrayList<>(List.of(one.get(), two.get()));
    Collections.sort(statuses);
    assertEquals(List.of(200, 400), statuses);
    assertEquals(
      0,
      get("/platform/catalog/books/" + id, null)
        .body.get("stock")
        .asInt()
    );
  }

  @Test
  void expiredQuizCannotBeSubmitted() throws Exception {
    var u = user();
    var session = req("POST", "/platform/quizzes/" + quiz + "/start", u, null)
      .body.get("sessionId")
      .asInt();
    new org.springframework.transaction.support.TransactionTemplate(
      transactions
    ).execute(status -> {
      var em =
        org.springframework.orm.jpa.EntityManagerFactoryUtils.getTransactionalEntityManager(
          emf
        );
      em.createQuery("update QuizSession s set s.expiresAt=:e where s.id=:id")
        .setParameter("e", java.time.LocalDateTime.now().minusMinutes(1))
        .setParameter("id", session)
        .executeUpdate();
      return null;
    });
    assertEquals(
      400,
      req(
        "POST",
        "/platform/quiz-sessions/" + session + "/submit",
        u,
        Map.of("answers", Map.of())
      ).status
    );
  }

  @Test
  void lockedAccountLosesExistingAccess() throws Exception {
    var u = user();
    var profile = get("/platform/me", u).body;
    assertEquals(
      200,
      req(
        "PUT",
        "/platform/admin/users/" + profile.get("id").asInt(),
        admin,
        Map.of(
          "fullName",
          "Locked",
          "email",
          profile.get("email").asText(),
          "role",
          "USER",
          "active",
          false
        )
      ).status
    );
    assertEquals(401, get("/platform/me", u).status);
    assertEquals(
      403,
      req(
        "POST",
        "/auth/login",
        null,
        Map.of(
          "userName",
          profile.get("userName").asText(),
          "passWord",
          "Student123!"
        )
      ).status
    );
  }

  @Test
  void postVisibilityAndCrud() throws Exception {
    var r = req(
      "POST",
      "/platform/admin/posts",
      admin,
      Map.of(
        "title",
        "Draft " + UUID.randomUUID(),
        "content",
        "Nội dung thông báo",
        "type",
        "NOTICE",
        "active",
        false
      )
    );
    assertEquals(200, r.status);
    int id = r.body.get("id").asInt();
    assertEquals(400, get("/platform/catalog/posts/" + id, null).status);
    assertEquals(
      200,
      req(
        "PUT",
        "/platform/admin/posts/" + id,
        admin,
        Map.of(
          "title",
          "Published",
          "content",
          "Thông báo mới",
          "type",
          "NOTICE",
          "active",
          true
        )
      ).status
    );
    assertEquals(
      "Published",
      get("/platform/catalog/posts/" + id, null)
        .body.get("title")
        .asText()
    );
    assertEquals(
      200,
      req("DELETE", "/platform/admin/posts/" + id, admin, null).status
    );
    assertEquals(404, get("/platform/catalog/posts/" + id, null).status);
  }

  @Test
  void recoveryUsesSingleUseTokenAndRevokesOldSession() throws Exception {
    var u = user();
    var profile = get("/platform/me", u).body;
    sent.clear();
    assertEquals(
      200,
      req(
        "POST",
        "/auth/forgot-password",
        null,
        Map.of("email", profile.get("email").asText())
      ).status
    );
    assertEquals(1, sent.size());
    String content = sent.poll().getText();
    var matcher = java.util.regex.Pattern.compile(
      "token=([a-f0-9]{64})"
    ).matcher(content);
    assertTrue(matcher.find());
    String token = matcher.group(1);
    var payload = Map.of(
      "token",
      token,
      "password",
      "ResetStudent123!",
      "confirmPassword",
      "ResetStudent123!"
    );
    assertEquals(
      200,
      req("POST", "/auth/reset-password", null, payload).status
    );
    assertEquals(401, get("/platform/me", u).status);
    assertEquals(
      400,
      req("POST", "/auth/reset-password", null, payload).status
    );
    assertNotNull(login(profile.get("userName").asText(), "ResetStudent123!"));
  }

  @Test
  void uploadRequiresLoginAndRejectsExecutableContent() throws Exception {
    assertEquals(401, req("POST", "/upload", null, null).status);
    String boundary = "testBoundary";
    String body =
      "--" +
      boundary +
      "\r\nContent-Disposition: form-data; name=\"file\"; filename=\"unsafe.png\"\r\nContent-Type: image/png\r\n\r\n<script>alert(1)</script>\r\n--" +
      boundary +
      "--\r\n";
    var r = http.send(
      HttpRequest.newBuilder(
        URI.create("http://localhost:" + port + "/api/upload")
      )
        .header("Authorization", "Bearer " + user())
        .header("Content-Type", "multipart/form-data; boundary=" + boundary)
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .build(),
      HttpResponse.BodyHandlers.ofString()
    );
    assertEquals(400, r.statusCode());
  }

  @Test
  void bookPrimaryImageAndGalleryAreStable() throws Exception {
    var r = req(
      "POST",
      "/platform/admin/books",
      admin,
      Map.of(
        "title",
        "Gallery test",
        "description",
        "Book with gallery",
        "price",
        100000,
        "stock",
        3,
        "categoryIds",
        List.of(1),
        "imageUrl",
        "/uploads/main.png",
        "images",
        List.of("/uploads/main.png", "/uploads/second.png")
      )
    );
    assertEquals(200, r.status);
    int id = r.body.get("id").asInt();
    assertEquals(
      "/uploads/main.png",
      get("/platform/catalog/books/" + id, null)
        .body.get("imageUrl")
        .asText()
    );
    assertEquals(2, r.body.get("images").size());
  }

  static final java.util.concurrent.atomic.AtomicLong sepayIds =
    new java.util.concurrent.atomic.AtomicLong(70000);

  Map<String, Object> transfer(JsonNode order, long id) {
    return new LinkedHashMap<>(
      Map.of(
        "id",
        id,
        "gateway",
        "MBBank",
        "accountNumber",
        "0123456789",
        "subAccount",
        "",
        "code",
        order.get("code").asText(),
        "content",
        "Thanh toan " + order.get("code").asText(),
        "transferType",
        "in",
        "transferAmount",
        order.get("total").decimalValue(),
        "referenceCode",
        "REF" + id
      )
    );
  }

  Reply sepay(String key, Map<String, Object> body) throws Exception {
    var b = HttpRequest.newBuilder(
      URI.create("http://localhost:" + port + "/api/payments/sepay/webhook")
    ).header("Content-Type", "application/json");
    if (key != null) b.header("Authorization", key);
    var r = http.send(
      b
        .POST(
          HttpRequest.BodyPublishers.ofString(json.writeValueAsString(body))
        )
        .build(),
      HttpResponse.BodyHandlers.ofString()
    );
    return new Reply(r.statusCode(), json.readTree(r.body()));
  }

  final String sepayKey = "Apikey test-sepay-webhook-secret-at-least-32-bytes";

  JsonNode createSepayOrder(String u, String type) throws Exception {
    add(u, type, type.equals("Book") ? book : course);
    var r = req(
      "POST",
      "/platform/orders",
      u,
      Map.of(
        "paymentMethod",
        "SEPAY",
        "fullName",
        "Người nhận",
        "phone",
        "0912345678",
        "address",
        "Hà Nội"
      )
    );
    assertEquals(200, r.status, r.body.toString());
    return r.body;
  }

  @Test
  void sepayPaymentMatchesServerAmountAndGrantsCourseExactlyOnce()
    throws Exception {
    var u = user();
    var o = createSepayOrder(u, "Course");
    int id = o.get("id").asInt();
    var before = get("/platform/orders/" + id + "/payment", u);
    assertTrue(before.body.get("available").asBoolean());
    String qr = before.body.get("qrUrl").asText();
    assertTrue(qr.startsWith("https://vietqr.app/img?"));
    assertTrue(qr.contains("acc=0123456789"));
    assertTrue(qr.contains(o.get("code").asText()));
    assertFalse(before.body.has("webhookKey"));
    var data = transfer(o, sepayIds.incrementAndGet());
    assertEquals(201, sepay(sepayKey, data).status);
    assertEquals(201, sepay(sepayKey, data).status);
    assertEquals(
      "paid",
      get("/platform/orders/" + id + "/payment", u)
        .body.get("paymentStatus")
        .asText()
    );
    assertTrue(
      get("/platform/orders/" + id + "/payment", u)
        .body.get("qrUrl")
        .isNull()
    );
    assertEquals(1, get("/platform/my-courses", u).body.size());
    assertEquals(200, get("/platform/learn/" + course, u).status);
  }

  @Test
  void sepayWebhookRejectsMissingOrBadAuthentication() throws Exception {
    var u = user();
    var o = createSepayOrder(u, "Course");
    var payload = transfer(o, sepayIds.incrementAndGet());
    assertEquals(401, sepay(null, payload).status);
    assertEquals(401, sepay("Apikey wrong", payload).status);
    assertEquals(401, sepay("Bearer " + admin, payload).status);
    assertEquals(
      "pending",
      get("/platform/orders/" + o.get("id").asInt() + "/payment", u)
        .body.get("paymentStatus")
        .asText()
    );
    assertEquals(0, get("/platform/my-courses", u).body.size());
  }

  @Test
  void sepayRejectsWrongAmountAccountGatewayAndOutflow() throws Exception {
    var u = user();
    var o = createSepayOrder(u, "Course");
    for (String field : List.of(
      "accountNumber",
      "gateway",
      "transferType",
      "transferAmount"
    )) {
      var p = transfer(o, sepayIds.incrementAndGet());
      p.put(
        field,
        field.equals("transferAmount")
          ? 1
          : field.equals("transferType")
            ? "out"
            : "WRONG"
      );
      assertEquals(201, sepay(sepayKey, p).status);
      assertEquals(
        "pending",
        get("/platform/orders/" + o.get("id").asInt() + "/payment", u)
          .body.get("paymentStatus")
          .asText()
      );
    }
    assertEquals(0, get("/platform/my-courses", u).body.size());
  }

  @Test
  void sepayDifferentTransferForAlreadyPaidOrderDoesNotGrantAgain()
    throws Exception {
    var u = user();
    var o = createSepayOrder(u, "Course");
    var first = transfer(o, sepayIds.incrementAndGet());
    assertEquals(201, sepay(sepayKey, first).status);
    var extra = transfer(o, sepayIds.incrementAndGet());
    assertEquals(201, sepay(sepayKey, extra).status);
    assertEquals(1, get("/platform/my-courses", u).body.size());
    var events = get("/platform/admin/sepay-transactions", admin).body;
    boolean review = false;
    for (var e : events)
      if (e.get("id").asLong() == ((Number) extra.get("id")).longValue()) {
        review = true;
        assertEquals("REVIEW", e.get("status").asText());
      }
    assertTrue(review);
  }

  @Test
  void sepayCanceledOrderCannotBecomePaid() throws Exception {
    var u = user();
    var o = createSepayOrder(u, "Book");
    int id = o.get("id").asInt();
    assertEquals(
      200,
      req(
        "POST",
        "/platform/orders/" + id + "/actions",
        u,
        Map.of("action", "CANCEL")
      ).status
    );
    assertEquals(
      201,
      sepay(sepayKey, transfer(o, sepayIds.incrementAndGet())).status
    );
    var status = get("/platform/orders/" + id + "/payment", u).body;
    assertEquals("CANCELED", status.get("status").asText());
    assertEquals("pending", status.get("paymentStatus").asText());
    assertFalse(status.get("available").asBoolean());
  }

  @Test
  void sepayPaymentInfoAndReconciliationAreProtected() throws Exception {
    var u = user();
    var o = createSepayOrder(u, "Course");
    int id = o.get("id").asInt();
    assertEquals(401, get("/platform/orders/" + id + "/payment", null).status);
    assertEquals(
      400,
      get("/platform/orders/" + id + "/payment", user()).status
    );
    assertEquals(403, get("/platform/admin/sepay-transactions", u).status);
  }

  @Test
  void simultaneousSepayRetriesAreAcknowledgedOnce() throws Exception {
    var u = user();
    var o = createSepayOrder(u, "Course");
    var payload = transfer(o, sepayIds.incrementAndGet());
    var a = java.util.concurrent.CompletableFuture.supplyAsync(() -> {
      try {
        return sepay(sepayKey, payload).status;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
    var b = java.util.concurrent.CompletableFuture.supplyAsync(() -> {
      try {
        return sepay(sepayKey, payload).status;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
    assertEquals(201, a.get());
    assertEquals(201, b.get());
    assertEquals(1, get("/platform/my-courses", u).body.size());
  }

  @Test
  void sepayUnknownOrAmbiguousOrderCodeIsNotCredited() throws Exception {
    var u = user();
    var o = createSepayOrder(u, "Course");
    var p = transfer(o, sepayIds.incrementAndGet());
    p.put("code", null);
    p.put("content", "Chuyen tien khong co ma");
    assertEquals(201, sepay(sepayKey, p).status);
    p = transfer(o, sepayIds.incrementAndGet());
    p.put("content", o.get("code").asText() + " JLFFFFFFFFFFFFFFFF");
    assertEquals(201, sepay(sepayKey, p).status);
    assertEquals(0, get("/platform/my-courses", u).body.size());
  }

  @Test
  void registrationErrorsAreActionableAndDoNotCreateAccount() throws Exception {
    String name = "new" + UUID.randomUUID().toString().replace("-", "");
    var payload = new LinkedHashMap<String, Object>(
      Map.of(
        "userName",
        name,
        "passWord",
        "Student123!",
        "confirmPassword",
        "Student123!",
        "fullName",
        "Học viên",
        "phone",
        "0912345678",
        "email",
        name + "@test.local"
      )
    );
    var mismatch = new LinkedHashMap<>(payload);
    mismatch.put("confirmPassword", "WrongPassword123!");
    var bad = req("POST", "/auth/register", null, mismatch);
    assertEquals(400, bad.status);
    assertTrue(bad.body.get("message").asText().contains("không khớp"));
    assertEquals(200, req("POST", "/auth/register", null, payload).status);
    var duplicated = req("POST", "/auth/register", null, payload);
    assertEquals(409, duplicated.status);
    assertTrue(duplicated.body.get("message").asText().contains("Email"));
    payload.put("email", "another" + name + "@test.local");
    var duplicateName = req("POST", "/auth/register", null, payload);
    assertEquals(409, duplicateName.status);
    assertTrue(
      duplicateName.body.get("message").asText().contains("Tên đăng nhập")
    );
  }

  @Test
  void onlyAdminAndUserRolesRemain() throws Exception {
    new org.springframework.transaction.support.TransactionTemplate(
      transactions
    ).execute(status -> {
      var em =
        org.springframework.orm.jpa.EntityManagerFactoryUtils.getTransactionalEntityManager(
          emf
        );
      assertEquals(
        Set.of("ADMIN", "USER"),
        new HashSet<>(
          em
            .createQuery("select r.roleName from Role r", String.class)
            .getResultList()
        )
      );
      return null;
    });
    var profile = get("/platform/me", admin).body;
    var invalid = req(
      "POST",
      "/platform/admin/users",
      admin,
      Map.of(
        "userName",
        "invalidTeacherRole",
        "fullName",
        "Role test",
        "email",
        "invalid-role@test.local",
        "password",
        "Student123!",
        "role",
        "TEACHER",
        "active",
        true
      )
    );
    assertEquals(400, invalid.status);
  }

  @org.springframework.beans.factory.annotation.Autowired
  com.example.jler_chinese_system.config.DataInitializer initializer;

  @Test
  void legacyRolesMigrateToUserAndInvalidatePreviousToken() throws Exception {
    var token = user();
    var before = get("/platform/me", token).body;
    int id = before.get("id").asInt();
    String name = before.get("userName").asText();
    new org.springframework.transaction.support.TransactionTemplate(
      transactions
    ).execute(status -> {
      var em =
        org.springframework.orm.jpa.EntityManagerFactoryUtils.getTransactionalEntityManager(
          emf
        );
      var role = new com.example.jler_chinese_system.entity.Role();
      role.setRoleName("TEACHER");
      em.persist(role);
      var u = em.find(com.example.jler_chinese_system.entity.User.class, id);
      u.setRole(role);
      return null;
    });
    String legacyToken = login(name, "Student123!");
    initializer.run();
    assertEquals(401, get("/platform/me", legacyToken).status);
    String renewed = login(name, "Student123!");
    assertEquals(
      "USER",
      get("/platform/me", renewed).body.get("role").asText()
    );
  }
}
