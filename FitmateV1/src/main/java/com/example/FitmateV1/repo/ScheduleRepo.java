package com.example.FitmateV1.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.FitmateV1.model.Schedule;
import com.example.FitmateV1.model.User;
import com.example.FitmateV1.model.Workout;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepo extends JpaRepository<Schedule, Long> {
	  @Query("SELECT s FROM Schedule s WHERE s.workout.createdBy.role.name = 'ADMIN'")
	    List<Schedule> findAllAdminWorkoutSchedules();
    @Query("SELECT s FROM Schedule s WHERE s.user.name = :username")
    List<Schedule> findAllByUser(@Param("username") String username);

	Optional<Schedule> findByUserAndWorkout(User user, Workout workout);

	boolean existsByUserAndWorkoutAndScheduledDate(User user, Workout workout, LocalDate today);

	List<Schedule> findByUserIdAndScheduledDate(Long id, LocalDate today);

	Iterable<? extends Schedule> findByWorkout(Workout workout);
}
