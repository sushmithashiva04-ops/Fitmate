package com.example.FitmateV1.POJO;

import lombok.Data;

@Data
public class UserDetailsRequest {
	private Integer age;
    private String gender;
    private String fitnessLevel;
    private Double height;
    private Double weight;

}
