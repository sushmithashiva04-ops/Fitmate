package com.example.FitmateV1.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.FitmateV1.model.Exercise;

@Repository
public interface ExerciseRepo extends JpaRepository<Exercise, Long> {
}
