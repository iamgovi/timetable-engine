package org.ideoholic.timetable.controller;

import org.ideoholic.timetable.dto.TimetableAllocationRequest;
import org.ideoholic.timetable.dto.TimetableAllocationResponse;
import org.ideoholic.timetable.dto.TimetableGenerationRequest;
import org.ideoholic.timetable.dto.SimpleTimetableGenerationRequest;
import org.ideoholic.timetable.service.TimetableAllocationService;
import org.ideoholic.timetable.service.TimetableAssignmentService;
import org.ideoholic.timetable.service.TimetableGenerationService;
import org.ideoholic.timetable.entity.TimetableAssignment;
import java.util.List;
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
}
