// service/UserService.java
package com.example.FitmateV1.service;

import com.example.FitmateV1.POJO.UserDetailsRequest;
import com.example.FitmateV1.model.User;
import com.example.FitmateV1.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    // ✅ Helper to get logged-in user from JWT
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepo.findByName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ✅ CREATE / ADD DETAILS
    public String addProfileDetails(UserDetailsRequest req) {
        User user = getCurrentUser();
        user.setAge(req.getAge());
        user.setGender(req.getGender());
        user.setFitnessLevel(req.getFitnessLevel());
        user.setHeight(req.getHeight());
        user.setWeight(req.getWeight());
        userRepo.save(user);
        return "Profile details added successfully";
    }

    // ✅ GET PROFILE DETAILS
    public User getProfileDetails() {
        return getCurrentUser();
    }

    // ✅ UPDATE PROFILE DETAILS
    public String updateProfileDetails(UserDetailsRequest req) {
        User user = getCurrentUser();
        if (req.getAge() != null) user.setAge(req.getAge());
        if (req.getGender() != null) user.setGender(req.getGender());
        if (req.getFitnessLevel() != null) user.setFitnessLevel(req.getFitnessLevel());
        if (req.getHeight() != null) user.setHeight(req.getHeight());
        if (req.getWeight() != null) user.setWeight(req.getWeight());

        userRepo.save(user);
        return "Profile details updated successfully";
    }
}
