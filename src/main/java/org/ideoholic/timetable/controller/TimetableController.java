package org.ideoholic.timetable.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.ideoholic.timetable.dto.FeasibilityRequest;
import org.ideoholic.timetable.dto.SchoolTimetableGenerationRequest;
import org.ideoholic.timetable.dto.TimetableAllocationRequest;
import org.ideoholic.timetable.dto.TimetableAllocationResponse;
import org.ideoholic.timetable.dto.TimetableGenerationRequest;
import org.ideoholic.timetable.dto.SimpleTimetableGenerationRequest;
import org.ideoholic.timetable.engine.feasibility.FeasibilityEngine;
import org.ideoholic.timetable.engine.feasibility.FeasibilityReport;
import org.ideoholic.timetable.engine.feasibility.ValidationIssue;
import org.ideoholic.timetable.engine.scheduler.GenerationSession;
import org.ideoholic.timetable.engine.scheduler.SchedulerReport;
import org.ideoholic.timetable.engine.scheduler.SchoolScheduler;
import org.ideoholic.timetable.service.TimetableAllocationService;
import org.ideoholic.timetable.service.TimetableAssignmentService;
import org.ideoholic.timetable.service.TimetableGenerationService;
import org.ideoholic.timetable.entity.TimetableAssignment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/timetable")
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableAllocationService allocationService;
    private final TimetableGenerationService generationService;
    private final TimetableAssignmentService assignmentService;
    private final FeasibilityEngine feasibilityEngine;
    private final SchoolScheduler schoolScheduler;

    @PostMapping("/allocate")
    public TimetableAllocationResponse allocate(
            @RequestBody TimetableAllocationRequest request) {

        return allocationService.allocate(request);
    }

    @PostMapping("/generate/monday")
    public Object generateMonday(
            @RequestBody TimetableGenerationRequest request) {

        return generationService.generateMondayTimetable(request);
    }

    @PostMapping("/generate/single-section")
    public Object generateSingleSection(
            @RequestBody SimpleTimetableGenerationRequest request) {

        return generationService.generateSingleSectionTimetable(request);
    }

    @PostMapping("/generate-school")
    public SchedulerReport generateSchool(
            @RequestBody SchoolTimetableGenerationRequest request) {

        FeasibilityReport feasibilityReport = feasibilityEngine.validate(feasibilityRequest(request));
        GenerationSession session = new GenerationSession();
        if (!feasibilityReport.isFeasible()) {
            session.fail();
            return SchedulerReport.feasibilityFailed(
                    session,
                    feasibilityWarnings(feasibilityReport));
        }

        return schoolScheduler.schedule(
                request.getAcademicYearId(),
                request.getClassIds(),
                request.getWorkingDayIds(),
                session);
    }

    @GetMapping("/assignments")
    public List<TimetableAssignment> getAllAssignments() {
        return assignmentService.findAll();
    }

    @GetMapping("/assignments/teacher/{teacherId}")
    public List<TimetableAssignment> getAssignmentsByTeacher(@PathVariable Long teacherId) {
        return assignmentService.findByTeacherId(teacherId);
    }

    @GetMapping("/assignments/section/{sectionId}")
    public List<TimetableAssignment> getAssignmentsBySection(@PathVariable Long sectionId) {
        return assignmentService.findBySectionId(sectionId);
    }

    private FeasibilityRequest feasibilityRequest(
            SchoolTimetableGenerationRequest request) {

        FeasibilityRequest feasibilityRequest = new FeasibilityRequest();
        feasibilityRequest.setAcademicYearId(request.getAcademicYearId());
        feasibilityRequest.setClassIds(request.getClassIds());
        feasibilityRequest.setWorkingDayIds(request.getWorkingDayIds());
        return feasibilityRequest;
    }

    private List<String> feasibilityWarnings(
            FeasibilityReport report) {

        return report.getErrors()
                .stream()
                .map(ValidationIssue::getMessage)
                .collect(Collectors.toList());
    }
}
