package com.example.FitmateV1.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ExerciseImg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String imageUrl; //
}
