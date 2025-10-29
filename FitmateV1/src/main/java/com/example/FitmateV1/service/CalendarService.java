package com.example.FitmateV1.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.FitmateV1.model.Schedule;
import com.example.FitmateV1.model.Status;
import com.example.FitmateV1.model.User;
import com.example.FitmateV1.repo.ScheduleRepo;
import com.example.FitmateV1.repo.StatusRepo;
import com.example.FitmateV1.repo.UserRepo;

import java.util.*;

@Service
public class CalendarService {

    @Autowired
    private ScheduleRepo scheduleRepository;

    @Autowired
    private StatusRepo statusRepository;
    @Autowired
    private UserRepo userRepo;
    
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("User not authenticated");
        }
        String username = auth.getName();
        return userRepo.findByName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    public List<Map<String, Object>> getUserSchedule() {
    	 User user = getCurrentUser();
        // Get all schedules for that user
        List<Schedule> schedules = scheduleRepository.findAllByUser(user.getName());
        List<Map<String, Object>> events = new ArrayList<>();

        for (Schedule s : schedules) {
            Map<String, Object> event = new HashMap<>();
            event.put("title", s.getWorkout().getName());
            event.put("start", s.getScheduledDate().toString());

            // get latest status if available
            Optional<Status> statusOpt = statusRepository.findLatestStatusForSchedule(s.getId());
            String statusColor = "#2979FF"; // default: blue (scheduled)
            if (statusOpt.isPresent()) {
                String status = statusOpt.get().getStatus().toUpperCase();
                statusColor = switch (status) {
                    case "DONE", "COMPLETED" -> "#00C853";   // green
                    case "PENDING" -> "#FFD600";             // yellow
                    case "MISSED" -> "#D50000";              // red
                    default -> "#2979FF";                    // blue
                };
            }

            event.put("color", statusColor);
            events.add(event);
        }

        return events;
    }
}
