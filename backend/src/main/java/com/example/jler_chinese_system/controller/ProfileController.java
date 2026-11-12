package com.example.jler_chinese_system.controller;

import com.example.jler_chinese_system.service.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/platform")
@RequiredArgsConstructor
public class ProfileController {

  private final AccountProfileService accountProfileService;

  @GetMapping("/me")
  public Object me() {
    return accountProfileService.profile();
  }

  @PutMapping("/me")
  public Object profile(@RequestBody Map<String, Object> d) {
    return accountProfileService.updateProfile(d);
  }

  @PutMapping("/me/password")
  public void password(@RequestBody Map<String, Object> d) {
    accountProfileService.changePassword(d);
  }
}
