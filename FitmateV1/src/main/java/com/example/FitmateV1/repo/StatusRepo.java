package com.example.FitmateV1.repo;



import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
//import org.springframework.data.repository.query;
import org.springframework.stereotype.Repository;

import com.example.FitmateV1.model.Schedule;
import com.example.FitmateV1.model.Status;
import com.example.FitmateV1.model.User;
import com.example.FitmateV1.model.Workout;

@Repository
public interface StatusRepo extends JpaRepository<Status, Long> {
    List<Status> findByWorkoutId(Long workoutId);

	Optional<Status> findByUserAndWorkout(User user, Workout workout);

	Iterable<? extends Status> findByWorkout(Workout workout);

	Optional<Status> findBySchedule(Schedule schedule);
	  // All statuses for a user
    List<Status> findByUserId(Long userId);


    @Query("SELECT s FROM Status s WHERE s.workout.createdBy.role.name = 'ADMIN' AND s.status = 'Completed'")
    List<Status> findAllCompletedAdminWorkouts();
    // For checking a user + workout combination
    Optional<Status> findByUserIdAndWorkoutId(Long userId, Long workoutId);

    // For streaks and stats
    List<Status> findByUserIdAndStatus(Long userId, String status);

	List<Status> findByUserIdAndStatusAndCompletedAtBetween(Long userId, String string, LocalDate weekAgo,
			LocalDate today);
	  @Query("SELECT st FROM Status st WHERE st.schedule.id = :scheduleId ORDER BY st.id DESC")
	    Optional<Status> findLatestStatusForSchedule(@Param("scheduleId") Long scheduleId);


    // 🔹 Count workouts by status
   }
