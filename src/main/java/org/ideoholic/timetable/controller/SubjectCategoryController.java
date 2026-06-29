package org.ideoholic.timetable.controller;

import java.util.List;

import javax.validation.Valid;

import org.ideoholic.timetable.dto.SubjectCategoryRequest;
import org.ideoholic.timetable.entity.SubjectCategory;
import org.ideoholic.timetable.service.SubjectCategoryService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/subject-categories")
@RequiredArgsConstructor
public class SubjectCategoryController {

    private final SubjectCategoryService service;

    @PostMapping
    public SubjectCategory create(
            @Valid @RequestBody SubjectCategoryRequest request) {

        return service.create(request);
    }

    @GetMapping
    public List<SubjectCategory> findAll() {

        return service.findAll();
    }

    @GetMapping("/{id}")
    public SubjectCategory findById(
            @PathVariable Long id) {

        return service.findById(id);
    }

    @PutMapping("/{id}")
    public SubjectCategory update(
            @PathVariable Long id,
            @Valid @RequestBody SubjectCategoryRequest request) {

        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id) {

        service.delete(id);
    }
}
