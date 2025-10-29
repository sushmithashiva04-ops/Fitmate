package com.example.FitmateV1.utils;



import com.example.FitmateV1.POJO.ExerciseResponse;
import com.example.FitmateV1.model.Exercise;
import com.example.FitmateV1.model.ExerciseImg;
import com.example.FitmateV1.model.Workout;
import com.example.FitmateV1.repo.ExerciseImgRepo;

import java.util.*;
import java.util.stream.Collectors;

public class WorkoutMapper {

    public static Map<String, Object> mapWorkoutToResponse(
            Workout workout,
            Map<String, String> exerciseImageMap
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", workout.getId());
        response.put("name", workout.getName());
        response.put("type", workout.getType());
        response.put("duration", workout.getDuration());

        List<ExerciseResponse> exerciseResponses = workout.getExercises().stream()
                .map(ex -> mapExerciseToResponse(ex, exerciseImageMap))
                .collect(Collectors.toList());

        response.put("exercises", exerciseResponses);
        return response;
    }

    private static ExerciseResponse mapExerciseToResponse(
            Exercise exercise,
            Map<String, String> exerciseImageMap
    ) {
        String imageUrl = exerciseImageMap.getOrDefault(
                exercise.getName().toLowerCase(),
                null
        );

        return ExerciseResponse.builder()
                .name(exercise.getName())
                .sets(exercise.getSets())
                .reps(exercise.getReps())
                .weight(exercise.getWeight())
                .imageUrl(imageUrl)
                .build();
    }

    public static Map<String, String> preloadExerciseImages(ExerciseImgRepo repo) {
        return repo.findAll().stream()
                .collect(Collectors.toMap(
                        e -> e.getName().toLowerCase(),
                        ExerciseImg::getImageUrl
                ));
    }
}
