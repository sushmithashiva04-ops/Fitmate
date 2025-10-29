
package com.example.FitmateV1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "exercise")
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;   // e.g., "Bench Press"
    private Integer sets;  // e.g., 3
    private Integer reps;  // e.g., 10
    private Double weight; // e.g., 40.5 (in kg)
    // ✅ Reference to admin exercise (if chosen)
   
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_id")
    @JsonBackReference
    private Workout workout;

    // Constructors
  }
