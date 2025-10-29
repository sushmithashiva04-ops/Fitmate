package com.example.FitmateV1.controller;



import com.example.FitmateV1.POJO.AdminWorkoutRequest;
import com.example.FitmateV1.service.AdminWorkoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/admin/workout")
public class AdminWorkoutController {

    @Autowired
    private AdminWorkoutService adminWorkoutService;
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public String addWorkout(@RequestBody AdminWorkoutRequest request) {
        return adminWorkoutService.addWorkout(request);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public String updateWorkout(@PathVariable Long id, @RequestBody AdminWorkoutRequest request) {
        return adminWorkoutService.updateWorkout(id, request);
    }
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping("/delete/{id}")
    public String deleteWorkout(@PathVariable Long id) {
        return adminWorkoutService.deleteWorkout(id);
    }
}
