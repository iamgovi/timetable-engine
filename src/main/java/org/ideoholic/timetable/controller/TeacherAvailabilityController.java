package org.ideoholic.timetable.controller;

import java.util.List;

import org.ideoholic.timetable.entity.TeacherAvailability;
import org.ideoholic.timetable.service.TeacherAvailabilityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/teacher-availability")
@RequiredArgsConstructor
public class TeacherAvailabilityController {

    private final TeacherAvailabilityService service;

    @PostMapping
    public TeacherAvailability save(
            @RequestBody TeacherAvailability availability) {

        return service.save(availability);
    }

    @GetMapping
    public List<TeacherAvailability> findAll() {

        return service.findAll();
    }
}