package com.example.jler_chinese_system.controller;

import com.example.jler_chinese_system.service.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/platform")
@RequiredArgsConstructor
public class AdminPlatformController {

  private final AdministrationService administrationService;
  private final ReportService reportService;

  @GetMapping("/admin/dashboard")
  public Object dashboard() {
    return administrationService.dashboard();
  }

  @GetMapping("/admin/{kind}")
  public Object list(@PathVariable String kind) {
    return administrationService.adminList(kind);
  }

  @PostMapping("/admin/{kind}")
  public Object create(
    @PathVariable String kind,
    @RequestBody Map<String, Object> d
  ) {
    return administrationService.adminSave(kind, null, d);
  }

  @PutMapping("/admin/{kind}/{id}")
  public Object update(
    @PathVariable String kind,
    @PathVariable Integer id,
    @RequestBody Map<String, Object> d
  ) {
    return administrationService.adminSave(kind, id, d);
  }

  @DeleteMapping("/admin/{kind}/{id}")
  public void delete(@PathVariable String kind, @PathVariable Integer id) {
    administrationService.adminDelete(kind, id);
  }

  @GetMapping("/admin/reports")
  public Object reports(
    @RequestParam(required = false) String from,
    @RequestParam(required = false) String to
  ) {
    return reportService.reports(from, to);
  }

  @PostMapping("/admin/quizzes/{id}/duplicate")
  public Object duplicate(@PathVariable Integer id) {
    return administrationService.cloneQuiz(id);
  }

  @PutMapping("/admin/quizzes/{id}/visibility")
  public Object visibility(
    @PathVariable Integer id,
    @RequestBody Map<String, Boolean> d
  ) {
    return administrationService.quizVisibility(
      id,
      Boolean.TRUE.equals(d.get("active"))
    );
  }
}
