package com.example.FitmateV1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.FitmateV1.service.DashBoardService;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/dashboard")
public class DashBoardController {

    @Autowired
    private DashBoardService dashBoardService;

    // 1️⃣ Get workout counts (Pending, Done, Scheduled)
    @GetMapping("/counts")
    public Map<String, Long> getWorkoutCounts() {
        return dashBoardService.getWorkoutCountsForUser();
    }

    // 2️⃣ Get total completed workout duration
    @GetMapping("/total-duration")
    public Double getTotalCompletedWorkoutDuration() {
        return dashBoardService.getTotalCompletedWorkoutDuration();
    }

    // 3️⃣ Get total calories burned
    @GetMapping("/total-calories")
    public Double getTotalCaloriesBurned() {
        return dashBoardService.getTotalCaloriesBurned();
    }

    // 4️⃣ Get daily completion streak
    @GetMapping("/streak")
    public int getDailyCompletionStreak() {
        return dashBoardService.getDailyCompletionStreak();
    }

    // 5️⃣ Get workout type distribution (Pie chart)
    @GetMapping("/workout-types")
    public Map<String, Long> getWorkoutTypeDistribution() {
        return dashBoardService.getWorkoutTypeDistribution();
    }

    // 6️⃣ Get line chart data for last 7 days
    @GetMapping("/weekly-stats")
    public Map<String, Object> getLast7DaysStats() {
        return dashBoardService.getLast7DaysStats();
    }
}
