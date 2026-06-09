package org.ideoholic.timetable.controller;

import java.util.List;

import org.ideoholic.timetable.entity.TeacherSubjectMapping;
import org.ideoholic.timetable.service.TeacherSubjectMappingService;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/teacher-subject-mappings")
@RequiredArgsConstructor
public class TeacherSubjectMappingController {

    private final TeacherSubjectMappingService service;

    @PostMapping
    public TeacherSubjectMapping save(
            @RequestBody TeacherSubjectMapping mapping) {

        return service.save(mapping);
    }

    @GetMapping
    public List<TeacherSubjectMapping> findAll() {

        return service.findAll();
    }
}