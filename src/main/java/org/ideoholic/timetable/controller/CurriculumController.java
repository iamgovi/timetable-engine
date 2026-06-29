package org.ideoholic.timetable.controller;

import java.util.List;

import javax.validation.Valid;

import org.ideoholic.timetable.dto.CurriculumRequest;
import org.ideoholic.timetable.dto.CurriculumSubjectRequest;
import org.ideoholic.timetable.entity.Curriculum;
import org.ideoholic.timetable.entity.CurriculumSubject;
import org.ideoholic.timetable.service.CurriculumService;
import org.ideoholic.timetable.service.CurriculumSubjectService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/curricula")
@RequiredArgsConstructor
public class CurriculumController {

    private final CurriculumService curriculumService;

    private final CurriculumSubjectService curriculumSubjectService;

    @PostMapping
    public Curriculum create(
            @Valid @RequestBody CurriculumRequest request) {

        return curriculumService.create(request);
    }

    @GetMapping
    public List<Curriculum> findAll(
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long academicYearId) {

        if (classId != null && academicYearId != null) {
            return java.util.Collections.singletonList(
                    curriculumService.findByClassAndAcademicYear(classId, academicYearId));
        }

        if (classId != null) {
            return curriculumService.findByClassId(classId);
        }

        if (academicYearId != null) {
            return curriculumService.findByAcademicYearId(academicYearId);
        }

        return curriculumService.findAll();
    }

    @GetMapping("/{id}")
    public Curriculum findById(
            @PathVariable Long id) {

        return curriculumService.findById(id);
    }

    @PutMapping("/{id}")
    public Curriculum update(
            @PathVariable Long id,
            @Valid @RequestBody CurriculumRequest request) {

        return curriculumService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id) {

        curriculumService.delete(id);
    }

    @PostMapping("/{curriculumId}/subjects")
    public CurriculumSubject addSubject(
            @PathVariable Long curriculumId,
            @Valid @RequestBody CurriculumSubjectRequest request) {

        return curriculumSubjectService.create(curriculumId, request);
    }

    @GetMapping("/{curriculumId}/subjects")
    public List<CurriculumSubject> findSubjects(
            @PathVariable Long curriculumId) {

        return curriculumSubjectService.findByCurriculum(curriculumId);
    }

    @GetMapping("/subjects/{id}")
    public CurriculumSubject findSubjectById(
            @PathVariable Long id) {

        return curriculumSubjectService.findById(id);
    }

    @PutMapping("/subjects/{id}")
    public CurriculumSubject updateSubject(
            @PathVariable Long id,
            @Valid @RequestBody CurriculumSubjectRequest request) {

        return curriculumSubjectService.update(id, request);
    }

    @DeleteMapping("/subjects/{id}")
    public void deleteSubject(
            @PathVariable Long id) {

        curriculumSubjectService.delete(id);
    }
}
