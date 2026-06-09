package org.ideoholic.timetable.controller;

import java.util.List;

import org.ideoholic.timetable.entity.Section;
import org.ideoholic.timetable.service.SectionService;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sections")
@RequiredArgsConstructor
public class SectionController {

    private final SectionService service;

    @PostMapping
    public Section save(
            @RequestBody Section section) {

        return service.save(section);
    }

    @GetMapping
    public List<Section> findAll() {

        return service.findAll();
    }
}