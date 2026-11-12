package com.example.jler_chinese_system.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public record SePayWebhook(
  @NotNull @Positive Long id,
  @NotBlank @Size(max = 100) String gateway,
  @NotBlank @Size(max = 100) String accountNumber,
  @Size(max = 100) String subAccount,
  @Size(max = 100) String code,
  @NotBlank @Size(max = 2000) String content,
  @NotBlank @Size(max = 10) String transferType,
  @NotNull
  @Positive
  @Digits(integer = 13, fraction = 2)
  BigDecimal transferAmount,
  @Size(max = 100) String referenceCode
) {}
