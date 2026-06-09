package org.ideoholic.timetable.controller;

import java.util.List;

import org.ideoholic.timetable.entity.Teacher;
import org.ideoholic.timetable.service.TeacherService;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService service;

    @PostMapping
    public Teacher save(
            @RequestBody Teacher teacher) {

        return service.save(teacher);
    }

    @GetMapping
    public List<Teacher> findAll() {

        return service.findAll();
    }
}