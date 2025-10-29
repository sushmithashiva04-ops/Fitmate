package com.example.FitmateV1.repo;



//import com.example.FitmateV1.model.Exercise;
import com.example.FitmateV1.model.ExerciseImg;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseImgRepo extends JpaRepository<ExerciseImg, Long> {

	Optional<ExerciseImg> findByNameIgnoreCase(String name);
}
