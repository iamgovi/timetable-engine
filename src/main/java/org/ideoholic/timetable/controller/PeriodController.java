package org.ideoholic.timetable.controller;

import java.util.List;

import org.ideoholic.timetable.entity.Period;
import org.ideoholic.timetable.service.PeriodService;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/periods")
@RequiredArgsConstructor
public class PeriodController {

    private final PeriodService service;

    @PostMapping
    public Period save(
            @RequestBody Period period) {

        return service.save(period);
    }

    @GetMapping
    public List<Period> findAll() {

        return service.findAll();
    }
}