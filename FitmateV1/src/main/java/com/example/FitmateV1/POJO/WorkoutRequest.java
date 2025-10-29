package com.example.FitmateV1.POJO;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class WorkoutRequest {
    private String name;
    private String type;
    private Integer duration; // in minutes
    private LocalDate date;
    private List<ExerciseRequest> exercises; // nested list

    }
