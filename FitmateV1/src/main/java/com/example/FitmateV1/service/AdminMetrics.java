package com.example.FitmateV1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.FitmateV1.repo.*;

@Service
public class AdminMetrics {

    @Autowired
    private WorkoutRepo workoutRepository;

    @Autowired
    private ScheduleRepo scheduleRepository;

    @Autowired
    private StatusRepo statusRepository;

    // 1️⃣ Total workouts created by admin
    public Long getTotalAdminWorkouts() {
        return (long) workoutRepository.findAllAdminWorkouts().size();
    }

    // 2️⃣ Total workouts scheduled by users (admin-created)
    public Long getTotalScheduledWorkouts() {
        return (long) scheduleRepository.findAllAdminWorkoutSchedules().size();
    }

    // 3️⃣ Completion rate (percentage)
    public Double getCompletionRate() {
        Long totalScheduled = (long) scheduleRepository.findAllAdminWorkoutSchedules().size();
        Long totalCompleted = (long) statusRepository.findAllCompletedAdminWorkouts().size();

        if (totalScheduled == 0) return 0.0;
        return (totalCompleted * 100.0) / totalScheduled;
    }
}
