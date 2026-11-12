package com.example.jler_chinese_system.service;

import com.example.jler_chinese_system.entity.*;
import jakarta.persistence.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RecoveryService {

  @PersistenceContext
  private EntityManager em;

  private final JavaMailSender mail;
  private final PasswordEncoder encoder;

  @Value("${app.frontend-url}")
  private String frontend;

  @Value("${spring.mail.username}")
  private String sender;

  private String hash(String token) {
    try {
      return HexFormat.of().formatHex(
        MessageDigest.getInstance("SHA-256").digest(
          token.getBytes(StandardCharsets.UTF_8)
        )
      );
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }

  public void forgot(String email) {
    var list = em
      .createQuery("from User u where lower(u.email)=:e", User.class)
      .setParameter("e", email.trim().toLowerCase(Locale.ROOT))
      .getResultList();
    if (list.isEmpty()) return;
    User u = list.get(0);
    em.lock(u, LockModeType.PESSIMISTIC_WRITE);
    var recent = em
      .createQuery(
        "select count(p) from PasswordReset p where p.userId=:u and p.expiresAt>:cut",
        Long.class
      )
      .setParameter("u", u.getUserId())
      .setParameter("cut", LocalDateTime.now().plusMinutes(13))
      .getSingleResult();
    if (recent > 0) return;
    byte[] random = new byte[32];
    new SecureRandom().nextBytes(random);
    String token = HexFormat.of().formatHex(random);
    var p = new PasswordReset();
    p.setUserId(u.getUserId());
    p.setTokenHash(hash(token));
    p.setExpiresAt(LocalDateTime.now().plusMinutes(15));
    em.persist(p);
    var message = new SimpleMailMessage();
    message.setFrom(sender);
    message.setTo(u.getEmail());
    message.setSubject("JLer – Đặt lại mật khẩu");
    message.setText(
      "Mở liên kết sau trong 15 phút để đặt lại mật khẩu:\n" +
        frontend.replaceAll("/+$", "") +
        "/reset-password?token=" +
        token +
        "\nNếu bạn không yêu cầu, hãy bỏ qua email này."
    );
    mail.send(message);
  }

  public void reset(String token, String password, String confirm) {
    com.example.jler_chinese_system.common.Checks.check(
      password.length() >= 8 &&
        password.length() <= 72 &&
        password.equals(confirm),
      "Mật khẩu từ 8 đến 72 ký tự và xác nhận phải trùng khớp"
    );
    var ps = em
      .createQuery(
        "from PasswordReset p where p.tokenHash=:h",
        PasswordReset.class
      )
      .setParameter("h", hash(token))
      .setLockMode(LockModeType.PESSIMISTIC_WRITE)
      .getResultList();
    com.example.jler_chinese_system.common.Checks.check(
      !ps.isEmpty(),
      "Liên kết không hợp lệ"
    );
    var p = ps.get(0);
    com.example.jler_chinese_system.common.Checks.check(
      !p.getUsed() && p.getExpiresAt().isAfter(LocalDateTime.now()),
      "Liên kết đã hết hạn hoặc đã sử dụng"
    );
    var u = em.find(User.class, p.getUserId());
    u.setPassWord(encoder.encode(password));
    u.setTokenVersion(u.getTokenVersion() + 1);
    em.createQuery("update PasswordReset p set p.used=true where p.userId=:u")
      .setParameter("u", u.getUserId())
      .executeUpdate();
    em.createQuery("delete from RefreshToken r where r.user=:u")
      .setParameter("u", u)
      .executeUpdate();
  }
}
