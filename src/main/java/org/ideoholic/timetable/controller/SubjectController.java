package org.ideoholic.timetable.controller;

import java.util.List;

import org.ideoholic.timetable.entity.Subject;
import org.ideoholic.timetable.service.SubjectService;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService service;

    @PostMapping
    public Subject save(
            @RequestBody Subject subject) {

        return service.save(subject);
    }

    @GetMapping
    public List<Subject> findAll() {

        return service.findAll();
    }
}