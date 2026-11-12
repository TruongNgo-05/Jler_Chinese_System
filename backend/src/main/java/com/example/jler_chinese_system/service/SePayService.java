package com.example.jler_chinese_system.service;

import static com.example.jler_chinese_system.common.ResponseMaps.map;

import com.example.jler_chinese_system.config.SePayProperties;
import com.example.jler_chinese_system.dto.SePayWebhook;
import com.example.jler_chinese_system.entity.*;
import jakarta.persistence.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Transactional
public class SePayService {

  @PersistenceContext
  private EntityManager em;

  private final SePayProperties config;
  private final OrderPaymentService payments;
  private static final Pattern ORDER_CODE = Pattern.compile(
    "(?<![A-Z0-9])(?:JL|TX)[A-F0-9]{16}(?![A-Z0-9])"
  );

  public void requireConfigured() {
    if (!config.isEnabled()) throw new ResponseStatusException(
      HttpStatus.SERVICE_UNAVAILABLE,
      "Thanh toán SePay chưa được cấu hình"
    );
  }

  public boolean enabled() {
    return config.isEnabled();
  }

  public void authenticate(String header) {
    requireConfigured();
    if (
      header == null ||
      !header.regionMatches(true, 0, "Apikey ", 0, 7) ||
      !MessageDigest.isEqual(
        header.substring(7).getBytes(StandardCharsets.UTF_8),
        config.getWebhookKey().getBytes(StandardCharsets.UTF_8)
      )
    ) throw new ResponseStatusException(
      HttpStatus.UNAUTHORIZED,
      "Webhook không hợp lệ"
    );
  }

  @Transactional(readOnly = true)
  public boolean exists(Long id) {
    return id != null && em.find(SePayTransaction.class, id) != null;
  }

  public void receive(SePayWebhook payload) {
    if (
      payload.transferAmount().stripTrailingZeros().scale() > 0
    ) throw new ResponseStatusException(
      HttpStatus.BAD_REQUEST,
      "Số tiền giao dịch phải là số nguyên đồng"
    );
    // The unique SePay ID is inserted and flushed before any financial mutation.
    // Concurrent duplicates roll back and are acknowledged by the controller only
    // after the original transaction has committed.
    if (exists(payload.id())) return;
    var event = new SePayTransaction();
    event.setId(payload.id());
    event.setAmount(payload.transferAmount());
    event.setGateway(payload.gateway());
    event.setAccountNumber(payload.accountNumber());
    event.setSubAccount(payload.subAccount());
    event.setContent(payload.content());
    event.setReferenceCode(payload.referenceCode());
    event.setStatus("REVIEW");
    em.persist(event);
    em.flush();
    if (!"in".equalsIgnoreCase(payload.transferType())) {
      finish(event, "IGNORED", "Giao dịch không phải tiền vào");
      return;
    }
    if (
      !same(config.getAccountNumber(), payload.accountNumber()) ||
      !same(config.getGateway(), payload.gateway()) ||
      (!config.getSubAccount().isBlank() &&
        !same(config.getSubAccount(), payload.subAccount()))
    ) {
      finish(event, "REVIEW", "Sai ngân hàng hoặc tài khoản nhận");
      return;
    }
    Set<String> codes = new LinkedHashSet<>();
    if (payload.code() != null && !payload.code().isBlank()) {
      String code = payload.code().trim().toUpperCase(Locale.ROOT);
      if (ORDER_CODE.matcher(code).matches()) codes.add(code);
    }
    var matcher = ORDER_CODE.matcher(
      payload.content().toUpperCase(Locale.ROOT)
    );
    while (matcher.find()) codes.add(matcher.group());
    if (codes.size() != 1) {
      finish(event, "REVIEW", "Thiếu hoặc có nhiều mã đơn trong nội dung");
      return;
    }
    String code = codes.iterator().next();
    event.setOrderCode(code);
    var orders = em
      .createQuery("from Order o where o.orderCode=:c", Order.class)
      .setParameter("c", code)
      .setLockMode(LockModeType.PESSIMISTIC_WRITE)
      .getResultList();
    if (orders.isEmpty()) {
      finish(event, "REVIEW", "Không tìm thấy đơn hàng");
      return;
    }
    Order order = orders.get(0);
    event.setOrderId(order.getOrderId());
    if (!"SEPAY".equals(order.getPaymentMethod())) {
      finish(event, "REVIEW", "Đơn không thanh toán bằng SePay");
      return;
    }
    if (
      Set.of("CANCELED", "COMPLETED").contains(order.getStatus()) &&
      order.getPaymentStatus() != Order.PaymentStatus.paid
    ) {
      finish(event, "REVIEW", "Đơn đã kết thúc hoặc bị hủy");
      return;
    }
    if (order.getPaymentStatus() == Order.PaymentStatus.paid) {
      finish(event, "REVIEW", "Đơn đã thanh toán; kiểm tra chuyển khoản thêm");
      return;
    }
    if (order.getTotalAmount().compareTo(payload.transferAmount()) != 0) {
      finish(event, "REVIEW", "Số tiền không khớp; cần đối soát thủ công");
      return;
    }
    payments.complete(order, "SEPAY:" + payload.id());
    finish(event, "ACCEPTED", "Đã xác nhận thanh toán");
  }

  private void finish(SePayTransaction t, String status, String reason) {
    t.setStatus(status);
    t.setReason(reason);
  }

  private boolean same(String a, String b) {
    return a != null && b != null && a.trim().equalsIgnoreCase(b.trim());
  }

  @Transactional(readOnly = true)
  public Map<String, Object> info(Order order) {
    boolean pending =
      order.getPaymentStatus() == Order.PaymentStatus.pending &&
      !Set.of("CANCELED", "COMPLETED").contains(order.getStatus());
    boolean available =
      config.isEnabled() && "SEPAY".equals(order.getPaymentMethod()) && pending;
    String qr = null;
    if (available) qr = UriComponentsBuilder.fromUriString(
      "https://vietqr.app/img"
    )
      .queryParam("acc", config.getAccountNumber())
      .queryParam("bank", config.getBankCode())
      .queryParam(
        "amount",
        order.getTotalAmount().toBigIntegerExact().toString()
      )
      .queryParam("des", order.getOrderCode())
      .queryParam("template", "compact")
      .queryParam("showinfo", "true")
      .queryParam("fullacc", "true")
      .queryParam("holder", config.getAccountName())
      .queryParam("store", "JLer")
      .build()
      .encode()
      .toUriString();
    return map(
      "orderId",
      order.getOrderId(),
      "code",
      order.getOrderCode(),
      "method",
      order.getPaymentMethod(),
      "status",
      order.getStatus(),
      "paymentStatus",
      order.getPaymentStatus(),
      "amount",
      order.getTotalAmount(),
      "available",
      available,
      "bank",
      config.getBankCode(),
      "accountNumber",
      config.getAccountNumber(),
      "accountName",
      config.getAccountName(),
      "transferContent",
      order.getOrderCode(),
      "qrUrl",
      qr
    );
  }

  @Transactional(readOnly = true)
  public List<?> transactions() {
    return em
      .createQuery(
        "from SePayTransaction t order by t.receivedAt desc",
        SePayTransaction.class
      )
      .getResultList()
      .stream()
      .map(t ->
        map(
          "id",
          t.getId(),
          "orderId",
          t.getOrderId(),
          "code",
          t.getOrderCode(),
          "amount",
          t.getAmount(),
          "gateway",
          t.getGateway(),
          "accountNumber",
          t.getAccountNumber(),
          "content",
          t.getContent(),
          "status",
          t.getStatus(),
          "reason",
          t.getReason(),
          "receivedAt",
          t.getReceivedAt()
        )
      )
      .toList();
  }
}
