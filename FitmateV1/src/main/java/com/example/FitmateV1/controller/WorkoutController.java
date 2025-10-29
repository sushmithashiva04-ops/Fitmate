package com.example.FitmateV1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.FitmateV1.POJO.WorkoutRequest;
import com.example.FitmateV1.model.Workout;
import com.example.FitmateV1.service.WorkoutService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    @Autowired
    private WorkoutService workoutService;

    // ✅ Add new workout (with exercises)
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/add")
    public String addWorkout(@RequestBody WorkoutRequest request) {
        return workoutService.addWorkout(request);
    }

    // ✅ Update workout
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/update/{id}")
    public String updateWorkout(@PathVariable Long id, @RequestBody WorkoutRequest request) {
        return workoutService.updateWorkout(id, request);
    }

    // ✅ Remove workout from user’s schedule
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/delete/{id}")
    public String deleteWorkout(@PathVariable Long id) {
        return workoutService.deleteWorkout(id);
    }

    // ✅ Remove a single exercise from a workout
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping("/{workoutId}/exercise/{exerciseId}")
    public String removeExercise(@PathVariable Long workoutId, @PathVariable Long exerciseId) {
        return workoutService.deleteExercise(workoutId, exerciseId);
    }

    // ✅ Get all workouts for the current user
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public  List<Map<String, Object>> getUserWorkouts() {
        return workoutService.getUserWorkouts();
    }

    // ✅ Get workouts scheduled for today (current user)
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/today")
    public List<Map<String, Object>> getTodayWorkouts() {
        return workoutService.getTodayWorkouts();
    }
    @PostMapping("/complete/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> markWorkoutComplete(
            @PathVariable Long id,
            @RequestBody Map<String, Double> request) {

        Double caloriesBurned = request.get("caloriesBurned");
        String message = workoutService.markWorkoutComplete(id, caloriesBurned);
        return ResponseEntity.ok(message);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/schedule-admin/{id}")
    public String scheduleAdminWorkout(@PathVariable Long id) {
        return workoutService.scheduleWorkoutForToday(id);
    }
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/reschedule/{id}")
    public String rescheduleWorkout(@PathVariable Long id, @RequestParam("newDate") String newDateStr) {
        return workoutService.rescheduleWorkout(id, LocalDate.parse(newDateStr));
    }


    // ✅ Get workouts created by admin (optional)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/admin")
    public List<Map<String, Object>> getAdminWorkouts() {
        return workoutService.getAdminWorkouts();
    }
}
