package com.example.FitmateV1.POJO;

public class ExerciseRequest {
    private String name;
    private Integer sets;
    private Integer reps;
    private Double weight; // in kg (optional)

    public ExerciseRequest() {}

    public ExerciseRequest(String name, Integer sets, Integer reps, Double weight) {
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSets() {
        return sets;
    }

    public void setSets(Integer sets) {
        this.sets = sets;
    }

    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
