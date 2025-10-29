package com.example.FitmateV1.POJO;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
public class ExerciseResponse {
    private String name;
    private Integer sets;
    private Integer reps;
    private Double weight;
    private String imageUrl;  

}
