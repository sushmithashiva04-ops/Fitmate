package com.example.FitmateV1.service;


import com.example.FitmateV1.POJO.AdminWorkoutRequest;
import com.example.FitmateV1.POJO.ExerciseRequest;
import com.example.FitmateV1.model.*;
import com.example.FitmateV1.repo.*;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AdminWorkoutService {

    @Autowired
    private WorkoutRepo workoutRepo;

    @Autowired
    private ExerciseRepo exerciseRepo;

    @Autowired
    private ScheduleRepo scheduleRepo;

    @Autowired
    private StatusRepo statusRepo;
    @Autowired
    private UserRepo userRepo;

    // 🔹 Helper to get current logged-in admin
    private User getCurrentAdmin() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByName(username)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
    }

    /**
     * ➕ Add workout (Admin only)
     * No date in request — global template
     */
    public String addWorkout(AdminWorkoutRequest request) {
        User admin = getCurrentAdmin();

        Workout workout = new Workout();
        workout.setName(request.getName());
        workout.setType(request.getType());
        workout.setDuration(request.getDuration());
        workout.setCreatedBy(admin);
      
        List<Exercise> exercises = new ArrayList<>();
        if (request.getExercises() == null || request.getExercises().isEmpty()) {
            throw new RuntimeException("Workout must have at least one exercise");
        }

        for (ExerciseRequest exReq : request.getExercises()) {
            Exercise ex = new Exercise();
            ex.setName(exReq.getName());
            ex.setSets(exReq.getSets());
            ex.setReps(exReq.getReps());
            ex.setWeight(exReq.getWeight());
            ex.setWorkout(workout);
            exercises.add(ex);
        }

        workout.setExercises(exercises);
        workoutRepo.save(workout);

        return "Workout added successfully by admin";
    }

    /**
     * ✏️ Update workout (Admin)
     */
    @Transactional
    public String updateWorkout(Long workoutId, AdminWorkoutRequest request) {
        User admin = getCurrentAdmin();

        Workout workout = workoutRepo.findById(workoutId)
                .orElseThrow(() -> new RuntimeException("Workout not found"));

        if (!workout.getCreatedBy().getId().equals(admin.getId())) {
            throw new RuntimeException("You can only edit workouts you created");
        }

        if (request.getName() != null) workout.setName(request.getName());
        if (request.getType() != null) workout.setType(request.getType());
        if (request.getDuration() != null) workout.setDuration(request.getDuration());

        List<Exercise> existingExercises = workout.getExercises();

        if (request.getExercises() != null) {
            // Update or add new
            for (ExerciseRequest exReq : request.getExercises()) {
                Optional<Exercise> matched = existingExercises.stream()
                        .filter(ex -> ex.getName().equalsIgnoreCase(exReq.getName()))
                        .findFirst();

                if (matched.isPresent()) {
                    Exercise ex = matched.get();
                    ex.setSets(exReq.getSets());
                    ex.setReps(exReq.getReps());
                    ex.setWeight(exReq.getWeight());
                } else {
                    Exercise newEx = new Exercise();
                    newEx.setName(exReq.getName());
                    newEx.setSets(exReq.getSets());
                    newEx.setReps(exReq.getReps());
                    newEx.setWeight(exReq.getWeight());
                    newEx.setWorkout(workout);
                    existingExercises.add(newEx);
                }
            }

            // Remove exercises not in request
            if (existingExercises.size() <= 1 && request.getExercises().size() < 1) {
                throw new RuntimeException("Workout must have at least one exercise");
            }

            existingExercises.removeIf(ex ->
                    request.getExercises().stream().noneMatch(req -> req.getName().equalsIgnoreCase(ex.getName()))
            );
        }

        workout.setExercises(existingExercises);
        workoutRepo.save(workout);

        return "Workout updated successfully";
    }

    /**
     * ❌ Delete workout (Admin)
     * Removes workout and its references from all schedules + statuses
     */
    @Transactional
    public String deleteWorkout(Long workoutId) {
        User admin = getCurrentAdmin();

        Workout workout = workoutRepo.findById(workoutId)
                .orElseThrow(() -> new RuntimeException("Workout not found"));

        if (!workout.getCreatedBy().getId().equals(admin.getId())) {
            throw new RuntimeException("You can only delete workouts you created");
        }

        // Delete related schedules
        scheduleRepo.deleteAll(scheduleRepo.findByWorkout(workout));

        // Delete related statuses
        statusRepo.deleteAll(statusRepo.findByWorkout(workout));

        // Delete exercises (cascade could handle it, but explicit ensures cleanup)
        exerciseRepo.deleteAll(workout.getExercises());

        // Finally delete workout
        workoutRepo.delete(workout);

        return "Workout and all user schedules/statuses deleted successfully";
    }
}
