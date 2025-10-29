package com.example.FitmateV1.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.FitmateV1.service.AdminMetrics;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/admin/metrics")
public class AdminMetricsController {

    @Autowired
    private AdminMetrics adminMetricsService;

    // 1️⃣ Total workouts created by admin
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/total-workouts")
    public Long getTotalWorkouts() {
        return adminMetricsService.getTotalAdminWorkouts();
    }

    // 2️⃣ Total workouts scheduled by users
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/scheduled-workouts")
    public Long getScheduledWorkouts() {
        return adminMetricsService.getTotalScheduledWorkouts();
    }

    // 3️⃣ Completion rate (%)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/completion-rate")
    public Double getCompletionRate() {
        return adminMetricsService.getCompletionRate();
    }
}
