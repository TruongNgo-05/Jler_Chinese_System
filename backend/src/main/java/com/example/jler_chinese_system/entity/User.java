package com.example.jler_chinese_system.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Integer userId;

  @Column(nullable = false, columnDefinition = "integer default 0")
  private Integer tokenVersion = 0;

  public Integer getTokenVersion() {
    return tokenVersion == null ? 0 : tokenVersion;
  }

  public void setTokenVersion(Integer version) {
    this.tokenVersion = version;
  }

  @Column(name = "email", length = 255, unique = true)
  private String email;

  @Column(name = "username", unique = true, length = 255)
  private String userName;

  @Column(name = "password", length = 512)
  @com.fasterxml.jackson.annotation.JsonIgnore
  private String passWord;

  @Column(name = "full_name", length = 255)
  private String fullName;

  @Column(name = "phone")
  private String phone;

  @Column(name = "status")
  private Boolean status;

  @Column(name = "address")
  private String address;

  @Column(name = "avater_url", length = 1000)
  private String avatarrUrl;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @ManyToOne(
    cascade = {
      CascadeType.PERSIST,
      CascadeType.DETACH,
      CascadeType.REFRESH,
      CascadeType.MERGE,
    }
  )
  @JoinColumn(name = "role_id", nullable = false)
  private Role role;

  @OneToOne(mappedBy = "user")
  private Cart cart;

  @OneToMany(
    mappedBy = "user",
    fetch = FetchType.LAZY,
    cascade = {
      CascadeType.PERSIST,
      CascadeType.DETACH,
      CascadeType.REFRESH,
      CascadeType.MERGE,
    }
  )
  private List<Order> orders;

  @OneToMany(
    mappedBy = "user",
    fetch = FetchType.LAZY,
    cascade = {
      CascadeType.PERSIST,
      CascadeType.DETACH,
      CascadeType.REFRESH,
      CascadeType.MERGE,
    }
  )
  private List<UserLessonProgress> userLessonProgresss;

  @OneToMany(
    mappedBy = "user",
    fetch = FetchType.LAZY,
    cascade = {
      CascadeType.PERSIST,
      CascadeType.DETACH,
      CascadeType.REFRESH,
      CascadeType.MERGE,
    }
  )
  private List<UserCourse> userCourses;

  @OneToMany(
    mappedBy = "user",
    fetch = FetchType.LAZY,
    cascade = {
      CascadeType.PERSIST,
      CascadeType.DETACH,
      CascadeType.REFRESH,
      CascadeType.MERGE,
    }
  )
  private List<QuizResult> quizResults;

  @OneToMany(
    mappedBy = "user",
    fetch = FetchType.LAZY,
    cascade = CascadeType.ALL
  )
  private List<Comment> comments = new ArrayList<>();

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  private List<RefreshToken> refreshtokens = new ArrayList<>();

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  @com.fasterxml.jackson.annotation.JsonIgnore
  public String getPassWord() {
    return passWord;
  }

  public void setPassWord(String passWord) {
    this.passWord = passWord;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public Boolean getStatus() {
    return status;
  }

  public void setStatus(Boolean status) {
    this.status = status;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getAvatarrUrl() {
    return avatarrUrl;
  }

  public void setAvatarrUrl(String avatarrUrl) {
    this.avatarrUrl = avatarrUrl;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public Cart getCart() {
    return cart;
  }

  public void setCart(Cart cart) {
    this.cart = cart;
  }

  public List<Order> getOrders() {
    return orders;
  }

  public void setOrders(List<Order> orders) {
    this.orders = orders;
  }

  public List<UserLessonProgress> getUserLessonProgresss() {
    return userLessonProgresss;
  }

  public void setUserLessonProgresss(
    List<UserLessonProgress> userLessonProgresss
  ) {
    this.userLessonProgresss = userLessonProgresss;
  }

  public List<UserCourse> getUserCourses() {
    return userCourses;
  }

  public void setUserCourses(List<UserCourse> userCourses) {
    this.userCourses = userCourses;
  }

  public List<QuizResult> getQuizResults() {
    return quizResults;
  }

  public void setQuizResults(List<QuizResult> quizResults) {
    this.quizResults = quizResults;
  }

  public List<Comment> getComments() {
    return comments;
  }

  public void setComments(List<Comment> comments) {
    this.comments = comments;
  }
}
