package com.example.FitmateV1.service;

import com.example.FitmateV1.POJO.WorkoutRequest;
import com.example.FitmateV1.model.*;
import com.example.FitmateV1.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class WorkoutService {

    @Autowired private WorkoutRepo workoutRepo;
    @Autowired private ExerciseRepo exerciseRepo;
    @Autowired private ExerciseImgRepo exerciseImgRepo;
    @Autowired private ScheduleRepo scheduleRepo;
    @Autowired private StatusRepo statusRepo;
    @Autowired private UserRepo userRepo;

    // 🔹 Helper: current logged-in user
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("User not authenticated");
        }
        String username = auth.getName();
        return userRepo.findByName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 🟢 Add workout
    public String addWorkout(WorkoutRequest request) {
        User user = getCurrentUser();

        Workout workout = new Workout();
        workout.setName(request.getName());
        workout.setType(request.getType());
        workout.setDuration(request.getDuration());
        workout.setCreatedBy(user);

        // 🏋️ Add exercises
        List<Exercise> exercises = request.getExercises().stream().map(req -> {
            Exercise ex = new Exercise();
            ex.setName(req.getName());
            ex.setSets(req.getSets());
            ex.setReps(req.getReps());
            ex.setWeight(req.getWeight());
            ex.setWorkout(workout);
            return ex;
        }).collect(Collectors.toList());

        workout.setExercises(exercises);
        workoutRepo.save(workout);

        // 🗓️ Schedule
        Schedule schedule = new Schedule();
        schedule.setWorkout(workout);
        schedule.setUser(user);
        schedule.setScheduledDate(request.getDate());
        scheduleRepo.save(schedule);

        // 🟡 Create status
        Status status = new Status();
        status.setUser(user);
        status.setWorkout(workout);
        status.setSchedule(schedule);
        status.setStatus("Scheduled");
        status.setScheduledDate(request.getDate());
        statusRepo.save(status);

        return "Workout added and scheduled successfully";
    }

    // ✏️ Update workout
    public String updateWorkout(Long id, WorkoutRequest request) {
        User user = getCurrentUser();
        Workout workout = workoutRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Workout not found"));

        if (!workout.getCreatedBy().getId().equals(user.getId())) {
            throw new RuntimeException("You can only edit your own workouts");
        }

        if (request.getName() != null) workout.setName(request.getName());
        if (request.getType() != null) workout.setType(request.getType());
        if (request.getDuration() != null) workout.setDuration(request.getDuration());

        // Replace exercises
        if (request.getExercises() != null && !request.getExercises().isEmpty()) {
            workout.getExercises().clear();
            List<Exercise> newExercises = request.getExercises().stream()
                    .map(exReq -> {
                        Exercise ex = new Exercise();
                        ex.setName(exReq.getName());
                        ex.setSets(exReq.getSets());
                        ex.setReps(exReq.getReps());
                        ex.setWeight(exReq.getWeight());
                        ex.setWorkout(workout);
                        return ex;
                    })
                    .collect(Collectors.toList());
            workout.getExercises().addAll(newExercises);
        }

        workoutRepo.save(workout);
        return "Workout updated successfully!";
    }

    // 🔁 Reschedule
    public String rescheduleWorkout(Long workoutId, LocalDate newDate) {
        User user = getCurrentUser();
        Workout workout = workoutRepo.findById(workoutId)
                .orElseThrow(() -> new RuntimeException("Workout not found"));

        Schedule schedule = scheduleRepo.findByUserAndWorkout(user, workout)
                .orElseThrow(() -> new RuntimeException("Workout not scheduled yet"));

        if (schedule.getScheduledDate().equals(newDate)) {
            return "Workout already scheduled for this date!";
        }

        schedule.setScheduledDate(newDate);
        scheduleRepo.save(schedule);

        statusRepo.findBySchedule(schedule).ifPresent(status -> {
            status.setScheduledDate(newDate);
            status.setStatus("Rescheduled");
            statusRepo.save(status);
        });

        return "Workout rescheduled successfully for " + newDate;
    }

    // ✅ Mark complete
    public String markWorkoutComplete(Long workoutId, Double caloriesBurned) {
        User user = getCurrentUser();
        Workout workout = workoutRepo.findById(workoutId)
                .orElseThrow(() -> new RuntimeException("Workout not found"));

        workout.setCaloriesBurned(caloriesBurned);
        workoutRepo.save(workout);

        Status status = statusRepo.findByUserAndWorkout(user, workout)
                .orElseThrow(() -> new RuntimeException("Status record not found"));

        status.setStatus("DONE");
        status.setCompletedAt(LocalDate.now());
        statusRepo.save(status);

        return "Workout marked as completed!";
    }

    // ❌ Delete workout
    public String deleteWorkout(Long workoutId) {
        User user = getCurrentUser();
        Workout workout = workoutRepo.findById(workoutId)
                .orElseThrow(() -> new RuntimeException("Workout not found"));

        scheduleRepo.findByUserAndWorkout(user, workout).ifPresent(schedule -> {
            statusRepo.findBySchedule(schedule).ifPresent(status -> statusRepo.delete(status));
            scheduleRepo.delete(schedule);
        });

        return "Workout removed from your schedule";
    }

    // 🏋️ Delete exercise
    public String deleteExercise(Long workoutId, Long exerciseId) {
        Workout workout = workoutRepo.findById(workoutId)
                .orElseThrow(() -> new RuntimeException("Workout not found"));
        Exercise exercise = exerciseRepo.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        if (!exercise.getWorkout().getId().equals(workoutId)) {
            throw new RuntimeException("This exercise does not belong to this workout");
        }

        if (workout.getExercises().size() <= 1) {
            return "❌ Cannot delete. A workout must have at least one exercise!";
        }

        workout.getExercises().remove(exercise);
        exerciseRepo.delete(exercise);
        workoutRepo.save(workout);

        return "✅ Exercise deleted successfully!";
    }

    // 🧩 Map to response DTO
    private Map<String, Object> mapWorkoutResponse(Workout workout) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", workout.getId());
        response.put("name", workout.getName());
        response.put("type", workout.getType());
        response.put("duration", workout.getDuration());
        List<Status> statuses = StreamSupport.stream(statusRepo.findByWorkout(workout).spliterator(), false)
                .collect(Collectors.toList());
        Status status = statuses.isEmpty() ? null : statuses.get(0);

        List<Schedule> schedules = StreamSupport.stream(scheduleRepo.findByWorkout(workout).spliterator(), false)
                .collect(Collectors.toList());
        Schedule schedule = schedules.isEmpty() ? null : schedules.get(0);
       
        response.put("status", status != null ? status.getStatus() : "Not Scheduled");
        response.put("scheduledDate", schedule != null ? schedule.getScheduledDate() : null);

        List<Map<String, Object>> exercises = workout.getExercises().stream().map(ex -> {
            Map<String, Object> exMap = new LinkedHashMap<>();
            exMap.put("name", ex.getName());
            exMap.put("sets", ex.getSets());
            exMap.put("reps", ex.getReps());
            exMap.put("weight", ex.getWeight());

            // 🖼️ get image from ExerciseImg table
            ExerciseImg img = exerciseImgRepo.findByNameIgnoreCase(ex.getName()).orElse(null);
            exMap.put("imageUrl", img != null ? img.getImageUrl() : "No image available");
            return exMap;
        }).collect(Collectors.toList());

        response.put("exercises", exercises);
        return response;
    }

    // 📋 Get user workouts
    public List<Map<String, Object>> getUserWorkouts() {
        User user = getCurrentUser();
        return workoutRepo.findByCreatedBy(user).stream()
                .map(this::mapWorkoutResponse)
                .collect(Collectors.toList());
    }

    // 📅 Get today's workouts
    public List<Map<String, Object>> getTodayWorkouts() {
        User user = getCurrentUser();
        LocalDate today = LocalDate.now();
        List<Schedule> schedules = scheduleRepo.findByUserIdAndScheduledDate(user.getId(), today);
        return schedules.stream()
                .map(Schedule::getWorkout)
                .map(this::mapWorkoutResponse)
                .collect(Collectors.toList());
    }
 // 📅 Schedule an existing workout for today
    public String scheduleWorkoutForToday(Long workoutId) {
        User user = getCurrentUser();
        Workout workout = workoutRepo.findById(workoutId)
                .orElseThrow(() -> new RuntimeException("Workout not found"));

        LocalDate today = LocalDate.now();

        // check if already scheduled
        boolean alreadyScheduled = scheduleRepo.existsByUserAndWorkoutAndScheduledDate(user, workout, today);
        if (alreadyScheduled) {
            return "Workout already scheduled for today!";
        }

        // create new schedule
        Schedule schedule = new Schedule();
        schedule.setUser(user);
        schedule.setWorkout(workout);
        schedule.setScheduledDate(today);
        scheduleRepo.save(schedule);

        // create or update status
        Status status = new Status();
        status.setUser(user);
        status.setWorkout(workout);
        status.setSchedule(schedule);
        status.setStatus("Scheduled");
        status.setScheduledDate(today);
        statusRepo.save(status);

        return "Workout successfully scheduled for today!";
    }

    // 👑 Get admin workouts
    public List<Map<String, Object>> getAdminWorkouts() {
        return workoutRepo.findAll().stream()
                .filter(w -> w.getCreatedBy().getRole() != null &&
                        w.getCreatedBy().getRole().getName().equalsIgnoreCase("ADMIN"))
                .map(this::mapWorkoutResponse)
                .collect(Collectors.toList());
    }
}
