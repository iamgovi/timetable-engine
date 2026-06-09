package org.ideoholic.timetable.controller;

import java.util.List;

import org.ideoholic.timetable.entity.AcademicYear;
import org.ideoholic.timetable.service.AcademicYearService;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/academic-years")
@RequiredArgsConstructor
public class AcademicYearController {

    private final AcademicYearService service;

    @PostMapping
    public AcademicYear save(
            @RequestBody AcademicYear year) {

        return service.save(year);
    }

    @GetMapping
    public List<AcademicYear> findAll() {

        return service.findAll();
    }
}