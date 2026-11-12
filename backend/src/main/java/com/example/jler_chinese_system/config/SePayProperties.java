package com.example.jler_chinese_system.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.sepay")
@Getter
@Setter
public class SePayProperties {

  private boolean enabled;
  private String webhookKey = "";
  private String bankCode = "";
  private String gateway = "";
  private String accountNumber = "";
  private String accountName = "";
  private String subAccount = "";

  @PostConstruct
  public void validate() {
    if (
      enabled &&
      (webhookKey.length() < 32 ||
        !accountNumber.matches("[A-Za-z0-9]{1,50}") ||
        bankCode.isBlank() ||
        gateway.isBlank() ||
        accountName.isBlank())
    ) throw new IllegalArgumentException(
      "SePay: cần webhook key tối thiểu 32 ký tự, bank-code, gateway, account-number và account-name"
    );
  }
}
