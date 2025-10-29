package com.example.FitmateV1.model;




import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    @Column(unique = true)
	    private String name;
	    
	    @Column(unique = true)
	    private String email;

	    private String password;

	    // Optional details filled after login
	    private Integer age;
	    private String gender;
	    private Double weight;
	    private Double height;
	    private String fitnessLevel;
	    @ManyToOne
	    @JoinColumn(name = "role_id")
	    private Role role;

    // Getters and setters
}
