// controller/UserDetailsController.java
package com.example.FitmateV1.controller;

import com.example.FitmateV1.POJO.UserDetailsRequest;
import com.example.FitmateV1.model.User;
import com.example.FitmateV1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*") // ✅ allow all origins (Swagger + frontend)
public class UserDetailsController {

    @Autowired
    private UserService userService;

    // ✅ ADD user profile details
    @PostMapping
    public ResponseEntity<String> addProfile(@RequestBody UserDetailsRequest request) {
        return ResponseEntity.ok(userService.addProfileDetails(request));
    }

    // ✅ GET logged-in user profile
    @GetMapping("/me")
    public ResponseEntity<User> getProfile() {
        return ResponseEntity.ok(userService.getProfileDetails());
    }

    // ✅ UPDATE user profile details
    @PutMapping
    public ResponseEntity<String> updateProfile(@RequestBody UserDetailsRequest request) {
        return ResponseEntity.ok(userService.updateProfileDetails(request));
    }
}
