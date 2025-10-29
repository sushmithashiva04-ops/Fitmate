package com.example.FitmateV1.controller;



import com.example.FitmateV1.model.Exercise;
import com.example.FitmateV1.model.ExerciseImg;
import com.example.FitmateV1.repo.ExerciseImgRepo;
import com.example.FitmateV1.service.S3Service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/exercises")
@CrossOrigin("*") // so frontend can call it
public class ExerciseImgController {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private ExerciseImgRepo exerciseRepository;
    @PostMapping(value = "/uploadExercise", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadExercise(
            @RequestParam("name") String name,
            @RequestParam("file") MultipartFile file) throws IOException {

        String imageUrl = s3Service.uploadFile(file);

        ExerciseImg exercise = new ExerciseImg();
        exercise.setName(name);
        exercise.setImageUrl(imageUrl);
        exerciseRepository.save(exercise);

        return ResponseEntity.ok("Exercise uploaded successfully!");
    }
    
 
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteExercise(@PathVariable Long id) {
        ExerciseImg exerciseImg = exerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        // ✅ If there's an image, delete it from S3
        if (exerciseImg.getImageUrl() != null) {
            String fileName = exerciseImg.getImageUrl().substring(exerciseImg.getImageUrl().lastIndexOf("/") + 1);
            s3Service.deleteFile(fileName);
        }

        // ✅ Delete from DB
   
        exerciseRepository.delete(exerciseImg);
        return ResponseEntity.ok("Exercise deleted successfully!");
    }


    @GetMapping("/exercises")
    public ResponseEntity<List<ExerciseImg>> getAllExercises() {
        return ResponseEntity.ok(exerciseRepository.findAll());
    }

    @GetMapping("/exercises/{id}")
    public ResponseEntity<ExerciseImg> getExercise(@PathVariable Long id) {
        ExerciseImg exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));
        return ResponseEntity.ok(exercise);
    }
    @PutMapping(value = "/exercises/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateExercise(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        ExerciseImg exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        exercise.setName(name);

        // If a new file is uploaded
        if (file != null && !file.isEmpty()) {
            // Delete old file
            String oldFile = exercise.getImageUrl().substring(exercise.getImageUrl().lastIndexOf("/") + 1);
            s3Service.deleteFile(oldFile);

            // Upload new one
            String newImageUrl = s3Service.uploadFile(file);
            exercise.setImageUrl(newImageUrl);
        }

        exerciseRepository.save(exercise);
        return ResponseEntity.ok("Exercise updated successfully!");
    }



}
