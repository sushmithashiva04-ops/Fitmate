package com.example.FitmateV1.controller;


import com.example.FitmateV1.POJO.AuthRequest;
import com.example.FitmateV1.POJO.RegisterRequest;
import com.example.FitmateV1.model.Role;
import com.example.FitmateV1.model.User;
import com.example.FitmateV1.repo.RoleRepo;
import com.example.FitmateV1.repo.UserRepo;
import com.example.FitmateV1.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    // ✅ REGISTER — simple success message
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        if (userRepo.findByName(request.getName()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Username already exists");
        }

        Role defaultRole = roleRepo.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Default role USER not found"));

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(defaultRole)
                .build();

        userRepo.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    // ✅ LOGIN — returns just token
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getName(), request.getPassword()
                )
        );

        User user = userRepo.findByName(request.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user.getName(), user.getRole().getName());
        return ResponseEntity.ok(token);
    }
}
