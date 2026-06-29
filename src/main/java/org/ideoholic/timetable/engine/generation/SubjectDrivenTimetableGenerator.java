package org.ideoholic.timetable.engine.generation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.ideoholic.timetable.dto.TimetableAllocationRequest;
import org.ideoholic.timetable.dto.TimetableAllocationResponse;
import org.ideoholic.timetable.entity.Period;
import org.ideoholic.timetable.entity.Section;
import org.ideoholic.timetable.entity.Subject;
import org.ideoholic.timetable.entity.Teacher;
import org.ideoholic.timetable.entity.TimetableAssignment;
import org.ideoholic.timetable.entity.WorkingDay;
import org.ideoholic.timetable.repository.TimetableAssignmentRepository;
import org.ideoholic.timetable.service.TimetableAllocationService;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SubjectDrivenTimetableGenerator
        implements TimetableGenerator {

    private final TimetableAllocationService allocationService;

    private final TimetableAssignmentRepository assignmentRepository;

    private final TeacherSubjectCatalog teacherSubjectCatalog;

    private final SubjectCandidateSelector subjectCandidateSelector;

    @Override
    public List<TimetableAssignment> generate(
            TimetableGenerationPlan plan) {

        TeacherSubjectCatalog.Catalog catalog = teacherSubjectCatalog.build(plan.getTeachers());
        List<TimetableAssignment> generated = new ArrayList<>();

        for (WorkingDay workingDay : plan.getWorkingDays()) {
            generated.addAll(generateForWorkingDay(
                    plan.getSections(),
                    plan.getPeriods(),
                    workingDay,
                    catalog.getSubjects(),
                    catalog.getTeachersBySubjectId()));
        }

        return generated;
    }

    private List<TimetableAssignment> generateForWorkingDay(
            List<Section> sections,
            List<Period> periods,
            WorkingDay workingDay,
            List<Subject> availableSubjects,
            Map<Long, List<Teacher>> teachersBySubjectId) {

        Map<Long, Set<Long>> occupiedTeacherIdsByPeriod = new HashMap<>();
        for (Period period : periods) {
            Set<Long> occupiedTeacherIds = assignmentRepository
                    .findByWorkingDayAndPeriod(workingDay, period)
                    .stream()
                    .filter(assignment -> assignment.getTeacher() != null)
                    .map(assignment -> assignment.getTeacher().getId())
                    .collect(Collectors.toSet());
            occupiedTeacherIdsByPeriod.put(period.getId(), occupiedTeacherIds);
        }

        List<TimetableAssignment> generated = new ArrayList<>();

        for (Section section : sections) {
            Map<Long, Integer> teacherUsageCount = new HashMap<>();
            Set<Long> teachersUsedInSection = new HashSet<>();
            Map<Long, Integer> weeklySubjectCount = new HashMap<>();
            Map<Long, Integer> daySubjectCount = new HashMap<>();

            List<TimetableAssignment> previousAssignments = assignmentRepository
                    .findBySectionAndWorkingDayIdLessThan(section, workingDay.getId());

            for (TimetableAssignment previousAssignment : previousAssignments) {
                if (previousAssignment.getSubject() != null
                        && previousAssignment.getSubject().getId() != null) {
                    Long subjectId = previousAssignment.getSubject().getId();
                    weeklySubjectCount.merge(subjectId, 1, Integer::sum);
                }
            }

            List<TimetableAssignment> currentDayAssignments = assignmentRepository
                    .findBySectionAndWorkingDay(section, workingDay);

            for (TimetableAssignment currentDayAssignment : currentDayAssignments) {
                if (currentDayAssignment.getSubject() != null
                        && currentDayAssignment.getSubject().getId() != null) {
                    Long subjectId = currentDayAssignment.getSubject().getId();
                    weeklySubjectCount.merge(subjectId, 1, Integer::sum);
                    daySubjectCount.merge(subjectId, 1, Integer::sum);
                }
            }

            for (Period period : periods) {
                Long periodId = period.getId();
                Set<Long> occupiedTeacherIds = occupiedTeacherIdsByPeriod
                        .computeIfAbsent(periodId, k -> new HashSet<>());
                TimetableAssignment previousDayPeriodAssignment = assignmentRepository
                        .findFirstBySectionAndPeriodAndWorkingDayIdLessThanOrderByWorkingDayIdDesc(
                                section, period, workingDay.getId());
                Long previousDayPeriodSubjectId = previousDayPeriodAssignment == null
                        || previousDayPeriodAssignment.getSubject() == null
                        ? null
                        : previousDayPeriodAssignment.getSubject().getId();
                Long previousPeriodSubjectId = findPreviousPeriodSubjectId(
                        section,
                        workingDay,
                        period);

                List<SubjectCandidate> candidateSubjects = subjectCandidateSelector.selectCandidates(
                        availableSubjects,
                        teachersBySubjectId,
                        occupiedTeacherIds,
                        weeklySubjectCount,
                        daySubjectCount,
                        previousPeriodSubjectId,
                        previousDayPeriodSubjectId);

                if (candidateSubjects.isEmpty()) {
                    continue;
                }

                boolean assigned = false;

                for (SubjectCandidate subjectCandidate : candidateSubjects) {
                    Subject subject = subjectCandidate.getSubject();
                    List<Teacher> subjectTeachers = teachersBySubjectId
                            .getOrDefault(subject.getId(), new ArrayList<>());

                    List<Teacher> eligibleTeachers = subjectTeachers.stream()
                            .filter(teacher -> !occupiedTeacherIds.contains(teacher.getId()))
                            .sorted(Comparator
                                    .comparingInt((Teacher teacher) -> teachersUsedInSection
                                            .contains(teacher.getId()) ? 1 : 0)
                                    .thenComparingInt(teacher -> teacherUsageCount.getOrDefault(
                                            teacher.getId(), 0))
                                    .thenComparingLong(Teacher::getId))
                            .collect(Collectors.toList());

                    for (Teacher teacher : eligibleTeachers) {
                        TimetableAllocationRequest allocationRequest = new TimetableAllocationRequest();
                        allocationRequest.setTeacherId(teacher.getId());
                        allocationRequest.setSubjectId(subject.getId());
                        allocationRequest.setSectionId(section.getId());
                        allocationRequest.setWorkingDayId(workingDay.getId());
                        allocationRequest.setPeriodId(periodId);

                        TimetableAllocationResponse response = allocationService.allocate(
                                allocationRequest);

                        if (Boolean.TRUE.equals(response.getSuccess())) {
                            TimetableAssignment assignment = assignmentRepository
                                    .findByTeacherAndWorkingDayAndPeriod(teacher, workingDay, period);

                            if (assignment != null) {
                                generated.add(assignment);
                                occupiedTeacherIds.add(teacher.getId());
                                teachersUsedInSection.add(teacher.getId());
                                teacherUsageCount.merge(teacher.getId(), 1, Integer::sum);
                                weeklySubjectCount.merge(subject.getId(), 1, Integer::sum);
                                daySubjectCount.merge(subject.getId(), 1, Integer::sum);
                                assigned = true;
                                break;
                            }
                        }
                    }

                    if (assigned) {
                        break;
                    }
                }

                if (!assigned) {
                    System.out.println(
                            "Warning: Could not assign subject for section=" + section.getId()
                                    + ", workingDay=" + workingDay.getId()
                                    + ", period=" + periodId);
                }
            }
        }

        return generated;
    }

    private Long findPreviousPeriodSubjectId(
            Section section,
            WorkingDay workingDay,
            Period period) {

        if (period == null || period.getPeriodNumber() == null
                || period.getPeriodNumber() <= 1) {
            return null;
        }

        TimetableAssignment previousPeriodAssignment = assignmentRepository
                .findBySectionAndWorkingDayAndPeriodPeriodNumber(
                        section,
                        workingDay,
                        period.getPeriodNumber() - 1);

        if (previousPeriodAssignment == null || previousPeriodAssignment.getSubject() == null) {
            return null;
        }

        return previousPeriodAssignment.getSubject().getId();
    }
}
