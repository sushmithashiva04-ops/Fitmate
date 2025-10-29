package com.example.FitmateV1.POJO;

import java.util.List;

import lombok.Data;

@Data
public class AdminWorkoutRequest {
	   private String name;
	    private String type;
	    private Integer duration; // in minutes
	   
	    private List<ExerciseRequest> exercises; // nested list

}
