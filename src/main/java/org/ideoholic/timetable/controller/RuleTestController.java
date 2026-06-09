package org.ideoholic.timetable.controller;

import org.ideoholic.timetable.dto.RuleTestRequest;
import org.ideoholic.timetable.dto.RuleTestResponse;
import org.ideoholic.timetable.engine.context.TimetableContext;
import org.ideoholic.timetable.engine.validators.RuleEvaluationService;
import org.ideoholic.timetable.entity.Period;
import org.ideoholic.timetable.entity.Section;
import org.ideoholic.timetable.entity.Subject;
import org.ideoholic.timetable.entity.Teacher;
import org.ideoholic.timetable.entity.WorkingDay;
import org.ideoholic.timetable.repository.PeriodRepository;
import org.ideoholic.timetable.repository.SectionRepository;
import org.ideoholic.timetable.repository.SubjectRepository;
import org.ideoholic.timetable.repository.TeacherRepository;
import org.ideoholic.timetable.repository.WorkingDayRepository;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rules")
@RequiredArgsConstructor
public class RuleTestController {

    private final RuleEvaluationService ruleEvaluationService;

    private final TeacherRepository teacherRepository;

    private final SubjectRepository subjectRepository;

    private final SectionRepository sectionRepository;

    private final WorkingDayRepository workingDayRepository;

    private final PeriodRepository periodRepository;

    @PostMapping("/validate")
    public RuleTestResponse validate(
            @RequestBody RuleTestRequest request) {

        Teacher teacher =
                teacherRepository.findById(
                        request.getTeacherId())
                        .orElse(null);

        Subject subject =
                subjectRepository.findById(
                        request.getSubjectId())
                        .orElse(null);

        Section section =
                sectionRepository.findById(
                        request.getSectionId())
                        .orElse(null);

        WorkingDay workingDay =
                workingDayRepository.findById(
                        request.getWorkingDayId())
                        .orElse(null);

        Period period =
                periodRepository.findById(
                        request.getPeriodId())
                        .orElse(null);

        TimetableContext context =
                new TimetableContext();

        context.setTeacher(teacher);
        context.setSubject(subject);
        context.setSection(section);
        context.setWorkingDay(workingDay);
        context.setPeriod(period);

        boolean result =
                ruleEvaluationService
                        .validate(context);

        return new RuleTestResponse(
                result,
                result
                        ? "Allocation Allowed"
                        : "Allocation Rejected");
    }
}