package com.example.jler_chinese_system.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {

  @NotBlank(message = "Tài khoản không được để trống")
  private String userName;

  @NotBlank(message = "Password không được để trống")
  private String passWord;

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getPassWord() {
    return passWord;
  }

  public void setPassWord(String passWord) {
    this.passWord = passWord;
  }
}
