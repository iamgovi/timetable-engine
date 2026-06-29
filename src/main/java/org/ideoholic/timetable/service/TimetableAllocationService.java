package org.ideoholic.timetable.service;

import org.ideoholic.timetable.dto.TimetableAllocationRequest;
import org.ideoholic.timetable.dto.TimetableAllocationResponse;
import org.ideoholic.timetable.engine.context.TimetableContext;
import org.ideoholic.timetable.engine.validators.RuleEvaluationService;
import org.ideoholic.timetable.entity.Period;
import org.ideoholic.timetable.entity.Section;
import org.ideoholic.timetable.entity.Subject;
import org.ideoholic.timetable.entity.Teacher;
import org.ideoholic.timetable.entity.TimetableAssignment;
import org.ideoholic.timetable.entity.WorkingDay;
import org.ideoholic.timetable.repository.PeriodRepository;
import org.ideoholic.timetable.repository.SectionRepository;
import org.ideoholic.timetable.repository.SubjectRepository;
import org.ideoholic.timetable.repository.TeacherRepository;
import org.ideoholic.timetable.repository.TimetableAssignmentRepository;
import org.ideoholic.timetable.repository.WorkingDayRepository;
import org.ideoholic.timetable.service.TeacherSubjectMappingService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TimetableAllocationService {

    private final RuleEvaluationService ruleEvaluationService;

    private final TeacherRepository teacherRepository;

    private final TeacherSubjectMappingService teacherSubjectMappingService;

    private final SubjectRepository subjectRepository;

    private final SectionRepository sectionRepository;

    private final WorkingDayRepository workingDayRepository;

    private final PeriodRepository periodRepository;

    private final TimetableAssignmentRepository assignmentRepository;

    public TimetableAllocationResponse allocate(
            TimetableAllocationRequest request) {

        Teacher teacher = teacherRepository.findById(request.getTeacherId()).orElse(null);

        Subject subject = resolveSubject(request);

        Section section = sectionRepository.findById(request.getSectionId()).orElse(null);

        WorkingDay workingDay = workingDayRepository.findById(request.getWorkingDayId()).orElse(null);

        Period period = periodRepository.findById(request.getPeriodId()).orElse(null);

        if (teacher == null) {
            return new TimetableAllocationResponse(false, "Teacher Not Found");
        }

        if (subject == null) {
            return new TimetableAllocationResponse(false, "Subject Not Found for Teacher");
        }

        if (section == null) {
            return new TimetableAllocationResponse(false, "Section Not Found");
        }

        if (workingDay == null) {
            return new TimetableAllocationResponse(false, "Working Day Not Found");
        }

        if (period == null) {
            return new TimetableAllocationResponse(false, "Period Not Found");
        }

        TimetableContext context = new TimetableContext();

        context.setTeacher(teacher);
        context.setSubject(subject);
        context.setSection(section);
        context.setWorkingDay(workingDay);
        context.setPeriod(period);

        // If identical assignment already exists, treat as idempotent success
        TimetableAssignment existing = assignmentRepository.findByTeacherAndWorkingDayAndPeriod(
            teacher, workingDay, period);//section ---- period

        if (existing != null) {
            boolean sameTeacher = existing.getTeacher() != null && teacher != null
                && existing.getTeacher().getId().equals(teacher.getId());
            boolean sameSection = existing.getSection() != null && section != null
                && existing.getSection().getId().equals(section.getId());
            boolean sameSubject = existing.getSubject() != null && subject != null
                && existing.getSubject().getId().equals(subject.getId());
            boolean samePeriod = existing.getPeriod() != null && period != null
                && existing.getPeriod().getId().equals(period.getId());
            boolean sameWorkingDay = existing.getWorkingDay() != null && workingDay != null
                && existing.getWorkingDay().getId().equals(workingDay.getId());

            if (sameTeacher && sameSection && sameSubject && samePeriod && sameWorkingDay) {
            return new TimetableAllocationResponse(true, "Allocation Already Exists");
            }
            // otherwise fall through to validation which will fail due to conflict
        }

        boolean valid = ruleEvaluationService.validate(context);

        if (!valid) {
            return new TimetableAllocationResponse(false, "Allocation Rejected");
        }

        TimetableAssignment assignment = new TimetableAssignment();
        assignment.setTeacher(teacher);
        assignment.setSubject(subject);
        assignment.setSection(section);
        assignment.setWorkingDay(workingDay);
        assignment.setPeriod(period);

        assignmentRepository.save(assignment);

        return new TimetableAllocationResponse(true, "Allocation Created");
    }

    private Subject resolveSubject(TimetableAllocationRequest request) {
        if (request.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(request.getSubjectId()).orElse(null);
            if (subject == null) {
                return null;
            }

            if (request.getTeacherId() != null) {
                return teacherSubjectMappingService.isTeacherMappedToSubject(
                        request.getTeacherId(), subject.getId())
                        ? subject
                        : null;
            }

            return subject;
        }

        if (request.getTeacherId() == null) {
            return null;
        }

        return teacherSubjectMappingService.findSubjectForTeacher(
                request.getTeacherId());
    }
}
