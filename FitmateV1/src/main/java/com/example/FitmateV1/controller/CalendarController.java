package com.example.FitmateV1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.FitmateV1.service.CalendarService;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    @Autowired
    private CalendarService calendarService;

    @GetMapping("/schedule")
    public ResponseEntity<List<Map<String, Object>>> getUserCalendar( ) {
               List<Map<String, Object>> events = calendarService.getUserSchedule();
        return ResponseEntity.ok(events);
    }
}
