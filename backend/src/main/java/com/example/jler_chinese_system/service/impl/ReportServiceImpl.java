package com.example.jler_chinese_system.service.impl;

import static com.example.jler_chinese_system.common.Checks.check;
import static com.example.jler_chinese_system.common.ResponseMaps.map;
import static com.example.jler_chinese_system.mapper.PlatformMapper.*;
import static com.example.jler_chinese_system.utils.RequestValues.*;

import com.example.jler_chinese_system.entity.*;
import com.example.jler_chinese_system.mapper.PlatformMapper;
import com.example.jler_chinese_system.repository.UserRepository;
import com.example.jler_chinese_system.security.CurrentUser;
import com.example.jler_chinese_system.service.*;
import com.example.jler_chinese_system.service.support.*;
import jakarta.persistence.*;
import java.math.*;
import java.time.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportServiceImpl implements ReportService {

  private final EntityLookup data;

  private LocalDate reportDate(String value) {
    try {
      return LocalDate.parse(value);
    } catch (java.time.format.DateTimeParseException e) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Ngày báo cáo không hợp lệ"
      );
    }
  }

  public Map<String, Object> reports(String from, String to) {
    LocalDate end =
      to == null || to.isBlank() ? LocalDate.now() : reportDate(to);
    LocalDate start =
      from == null || from.isBlank() ? end.minusDays(29) : reportDate(from);
    check(
      !start.isAfter(end) &&
        java.time.temporal.ChronoUnit.DAYS.between(start, end) <= 366,
      "Chọn khoảng ngày hợp lệ, tối đa 367 ngày"
    );
    var days = new LinkedHashMap<LocalDate, BigDecimal>();
    var counts = new HashMap<LocalDate, Integer>();
    for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) days.put(
      d,
      BigDecimal.ZERO
    );
    var top = new LinkedHashMap<String, Map<String, Object>>();
    BigDecimal total = BigDecimal.ZERO;
    int paid = 0,
      unknown = 0;
    for (var order : data.all(Order.class)) {
      if (order.getPaymentStatus() != Order.PaymentStatus.paid) continue;
      if (order.getPaymentAt() == null) {
        unknown++;
        continue;
      }
      LocalDate day = order.getPaymentAt().toLocalDate();
      if (!days.containsKey(day)) continue;
      total = total.add(order.getTotalAmount());
      paid++;
      days.put(day, days.get(day).add(order.getTotalAmount()));
      counts.merge(day, 1, Integer::sum);
      for (var item : order.getOderItems()) {
        String key = item.getItemType() + ":" + item.getItemId();
        var row = top.computeIfAbsent(key, k ->
          map(
            "title",
            item.getItemTitle(),
            "type",
            item.getItemType(),
            "quantity",
            0
          )
        );
        row.put("quantity", (Integer) row.get("quantity") + item.getQuantity());
      }
    }
    return map(
      "from",
      start,
      "to",
      end,
      "revenue",
      total,
      "paidOrders",
      paid,
      "unknownPaymentDates",
      unknown,
      "daily",
      days
        .entrySet()
        .stream()
        .map(e ->
          map(
            "date",
            e.getKey(),
            "revenue",
            e.getValue(),
            "orders",
            counts.getOrDefault(e.getKey(), 0)
          )
        )
        .toList(),
      "topItems",
      top
        .values()
        .stream()
        .sorted((a, b) ->
          Integer.compare(
            (Integer) b.get("quantity"),
            (Integer) a.get("quantity")
          )
        )
        .limit(10)
        .toList()
    );
  }
}
