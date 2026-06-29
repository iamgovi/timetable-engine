package org.ideoholic.timetable.controller;

import java.util.List;

import org.ideoholic.timetable.dto.FeasibilityRequest;
import org.ideoholic.timetable.engine.feasibility.FeasibilityEngine;
import org.ideoholic.timetable.engine.feasibility.FeasibilityReport;
import org.ideoholic.timetable.engine.planning.AcademicPlanningService;
import org.ideoholic.timetable.engine.planning.models.AcademicPlan;
import org.ideoholic.timetable.engine.planning.models.ClassDemand;
import org.ideoholic.timetable.engine.planning.models.PlanningFilter;
import org.ideoholic.timetable.engine.planning.models.PlanningSummary;
import org.ideoholic.timetable.engine.planning.models.SubjectDemand;
import org.ideoholic.timetable.engine.planning.models.TeacherRequirement;
import org.ideoholic.timetable.engine.planning.models.TeacherUtilization;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/planning")
@RequiredArgsConstructor
public class PlanningController {

    private final AcademicPlanningService planningService;

    private final FeasibilityEngine feasibilityEngine;

    @GetMapping
    public AcademicPlan plan(
            @RequestParam(required = false) Long academicYearId,
            @RequestParam(required = false) List<Long> classIds,
            @RequestParam(required = false) List<Long> sectionIds) {

        return planningService.plan(filter(academicYearId, classIds, sectionIds));
    }

    @GetMapping("/summary")
    public PlanningSummary summary(
            @RequestParam(required = false) Long academicYearId,
            @RequestParam(required = false) List<Long> classIds,
            @RequestParam(required = false) List<Long> sectionIds) {

        return planningService.plan(filter(academicYearId, classIds, sectionIds)).getSummary();
    }

    @GetMapping("/subjects")
    public List<SubjectDemand> subjects(
            @RequestParam(required = false) Long academicYearId,
            @RequestParam(required = false) List<Long> classIds,
            @RequestParam(required = false) List<Long> sectionIds) {

        return planningService.plan(filter(academicYearId, classIds, sectionIds)).getSubjectDemands();
    }

    @GetMapping("/teachers")
    public List<TeacherUtilization> teachers(
            @RequestParam(required = false) Long academicYearId,
            @RequestParam(required = false) List<Long> classIds,
            @RequestParam(required = false) List<Long> sectionIds) {

        return planningService.plan(filter(academicYearId, classIds, sectionIds)).getTeacherUtilizations();
    }

    @GetMapping("/teacher-requirements")
    public List<TeacherRequirement> teacherRequirements(
            @RequestParam(required = false) Long academicYearId,
            @RequestParam(required = false) List<Long> classIds,
            @RequestParam(required = false) List<Long> sectionIds) {

        return planningService.plan(filter(academicYearId, classIds, sectionIds)).getTeacherRequirements();
    }

    @GetMapping("/classes")
    public List<ClassDemand> classes(
            @RequestParam(required = false) Long academicYearId,
            @RequestParam(required = false) List<Long> classIds,
            @RequestParam(required = false) List<Long> sectionIds) {

        return planningService.plan(filter(academicYearId, classIds, sectionIds)).getClassDemands();
    }

    @PostMapping("/feasibility")
    public FeasibilityReport feasibility(
            @RequestBody FeasibilityRequest request) {

        return feasibilityEngine.validate(request);
    }

    private PlanningFilter filter(
            Long academicYearId,
            List<Long> classIds,
            List<Long> sectionIds) {

        PlanningFilter filter = new PlanningFilter();
        filter.setAcademicYearId(academicYearId);
        filter.setClassIds(classIds);
        filter.setSectionIds(sectionIds);
        return filter;
    }
}
