package org.ideoholic.timetable.controller;

import java.util.List;

import org.ideoholic.timetable.entity.WorkingDay;
import org.ideoholic.timetable.service.WorkingDayService;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/working-days")
@RequiredArgsConstructor
public class WorkingDayController {

    private final WorkingDayService service;

    @PostMapping
    public WorkingDay save(
            @RequestBody WorkingDay workingDay) {

        return service.save(workingDay);
    }

    @GetMapping
    public List<WorkingDay> findAll() {

        return service.findAll();
    }
}