package org.ideoholic.timetable.controller;

import java.util.List;

import org.ideoholic.timetable.entity.ClassMaster;
import org.ideoholic.timetable.service.ClassMasterService;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassMasterController {

    private final ClassMasterService service;

    @PostMapping
    public ClassMaster save(
            @RequestBody ClassMaster classMaster) {

        return service.save(classMaster);
    }

    @GetMapping
    public List<ClassMaster> findAll() {

        return service.findAll();
    }
}