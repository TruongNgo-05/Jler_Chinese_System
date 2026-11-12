package com.example.jler_chinese_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequestDTO {

  @NotBlank(message = "Tên đăng nhập không được để trống")
  @Size(min = 3, max = 50, message = "Tên đăng nhập từ 3 đến 50 ký tự")
  @jakarta.validation.constraints.Pattern(
    regexp = "[A-Za-z0-9._-]+",
    message = "Tên đăng nhập chỉ gồm chữ không dấu, số, dấu chấm, gạch dưới hoặc gạch ngang"
  )
  private String userName;

  @NotBlank(message = "Password không được để trống")
  @Size(min = 8, max = 72, message = "Mật khẩu từ 8 đến 72 ký tự")
  private String passWord;

  @NotBlank(message = "Password không được để trống")
  @Size(min = 8, max = 72, message = "Mật khẩu từ 8 đến 72 ký tự")
  private String confirmPassword;

  @NotBlank(message = "Họ tên không được để trống")
  @Size(max = 255, message = "Họ tên tối đa 255 ký tự")
  private String fullName;

  @Email(message = "Email không đúng định dạng")
  @Size(max = 255, message = "Email tối đa 255 ký tự")
  @NotBlank(message = "Email không được để trống")
  private String email;

  @NotBlank(message = "Số điện thoại không được để trống")
  @jakarta.validation.constraints.Pattern(
    regexp = "[+0-9 .-]{8,15}",
    message = "Số điện thoại từ 8 đến 15 ký tự"
  )
  private String phone;

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName == null ? null : userName.trim();
  }

  public String getPassWord() {
    return passWord;
  }

  public void setPassWord(String passWord) {
    this.passWord = passWord;
  }

  public String getConfirmPassword() {
    return confirmPassword;
  }

  public void setConfirmPassword(String confirmPassword) {
    this.confirmPassword = confirmPassword;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email == null ? null : email.trim();
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone == null ? null : phone.trim();
  }
}
