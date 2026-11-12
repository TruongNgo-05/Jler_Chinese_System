package com.example.jler_chinese_system.controller;

import com.example.jler_chinese_system.dto.SePayWebhook;
import com.example.jler_chinese_system.service.SePayService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SePayController {

  private final SePayService sepay;

  @PostMapping("/api/payments/sepay/webhook")
  public ResponseEntity<?> webhook(
    @RequestHeader(value = "Authorization", required = false) String auth,
    @Valid @RequestBody SePayWebhook payload
  ) {
    sepay.authenticate(auth);
    try {
      sepay.receive(payload);
    } catch (
      DataIntegrityViolationException
      | jakarta.persistence.PersistenceException e
    ) {
      if (!sepay.exists(payload.id())) throw e;
    }
    return ResponseEntity.status(201).body(Map.of("success", true));
  }
}
