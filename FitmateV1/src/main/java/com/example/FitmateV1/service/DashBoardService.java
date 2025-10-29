package com.example.FitmateV1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.FitmateV1.model.Status;
import com.example.FitmateV1.model.User;
import com.example.FitmateV1.model.Workout;
import com.example.FitmateV1.repo.StatusRepo;
import com.example.FitmateV1.repo.UserRepo;
import com.example.FitmateV1.repo.WorkoutRepo;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashBoardService {

    @Autowired
    private WorkoutRepo workoutRepo;
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private StatusRepo statusRepo;
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("User not authenticated");
        }
        String username = auth.getName();
        return userRepo.findByName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 1️⃣ Get workout counts by status (PENDING, DONE, SCHEDULED)
    public Map<String, Long> getWorkoutCountsForUser() {
    	 User user = getCurrentUser();
    	 
        List<Status> statuses = statusRepo.findByUserId(user.getId());

        Map<String, Long> counts = new HashMap<>();
        counts.put("PENDING", statuses.stream().filter(s -> "PENDING".equalsIgnoreCase(s.getStatus())).count());
        counts.put("DONE", statuses.stream().filter(s -> "DONE".equalsIgnoreCase(s.getStatus())).count());
        counts.put("SCHEDULED", statuses.stream().filter(s -> "SCHEDULED".equalsIgnoreCase(s.getStatus())).count());

        return counts;
    }

    // 2️⃣ Total duration of completed workouts
    public Double getTotalCompletedWorkoutDuration() {
    	 User user = getCurrentUser();
        List<Status> doneStatuses = statusRepo.findByUserIdAndStatus(user.getId(), "DONE");

        return doneStatuses.stream()
                .map(Status::getWorkout)
                .filter(Objects::nonNull)
                .mapToDouble(w -> w.getDuration() != null ? w.getDuration() : 0.0)
                .sum();
    }

    // 3️⃣ Total calories burned
    public Double getTotalCaloriesBurned() {
    	 User user = getCurrentUser();
        List<Status> doneStatuses = statusRepo.findByUserIdAndStatus(user.getId(), "DONE");

        return doneStatuses.stream()
                .map(Status::getWorkout)
                .filter(Objects::nonNull)
                .mapToDouble(w -> w.getCaloriesBurned() != null ? w.getCaloriesBurned() : 0.0)
                .sum();
    }

    // 4️⃣ Daily completion streak
    public int getDailyCompletionStreak() {
    	 User user = getCurrentUser();
        List<Status> doneStatuses = statusRepo.findByUserIdAndStatus(user.getId(), "DONE");

        Set<LocalDate> completedDates = doneStatuses.stream()
                .map(Status::getCompletedAt)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        LocalDate today = LocalDate.now();
        int streak = 0;

        while (completedDates.contains(today.minusDays(streak))) {
            streak++;
        }

        return streak;
    }

    // 5️⃣ Workout type distribution (Pie chart)
    public Map<String, Long> getWorkoutTypeDistribution( ) {
    	 User user = getCurrentUser();
        List<Status> doneStatuses = statusRepo.findByUserIdAndStatus(user.getId(), "DONE");

        return doneStatuses.stream()
                .map(Status::getWorkout)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        Workout::getType,
                        Collectors.counting()
                ));
    }

    // 6️⃣ Line chart - Calories & Duration (past 7 days)
    public Map<String, Object> getLast7DaysStats() {
    	 User user = getCurrentUser();
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6);

        List<Status> doneStatuses = statusRepo.findByUserIdAndStatusAndCompletedAtBetween(user.getId(), "DONE", weekAgo, today);

        Map<LocalDate, List<Status>> grouped = doneStatuses.stream()
                .collect(Collectors.groupingBy(Status::getCompletedAt));

        List<String> dates = new ArrayList<>();
        List<Double> calories = new ArrayList<>();
        List<Double> durations = new ArrayList<>();

        for (LocalDate date = weekAgo; !date.isAfter(today); date = date.plusDays(1)) {
            List<Status> dayStatuses = grouped.getOrDefault(date, new ArrayList<>());

            double totalCal = dayStatuses.stream()
                    .map(Status::getWorkout)
                    .filter(Objects::nonNull)
                    .mapToDouble(w -> w.getCaloriesBurned() != null ? w.getCaloriesBurned() : 0.0)
                    .sum();

            double totalDur = dayStatuses.stream()
                    .map(Status::getWorkout)
                    .filter(Objects::nonNull)
                    .mapToDouble(w -> w.getDuration() != null ? w.getDuration() : 0.0)
                    .sum();

            dates.add(date.toString());
            calories.add(totalCal);
            durations.add(totalDur);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("dates", dates);
        response.put("calories", calories);
        response.put("durations", durations);

        return response;
    }
}
