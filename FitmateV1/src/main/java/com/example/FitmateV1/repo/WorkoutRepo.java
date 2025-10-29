package com.example.FitmateV1.repo;

import com.example.FitmateV1.model.User;
import com.example.FitmateV1.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkoutRepo extends JpaRepository<Workout, Long> {

	   // 🔹 Total duration of completed workouts for user
	 @Query("SELECT w FROM Workout w WHERE w.createdBy.role.name = 'ADMIN'")
	    List<Workout> findAllAdminWorkouts();
	List<Workout> findByCreatedBy(User user);
}
