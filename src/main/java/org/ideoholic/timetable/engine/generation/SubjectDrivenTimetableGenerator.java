package org.ideoholic.timetable.engine.generation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.ideoholic.timetable.dto.TimetableAllocationRequest;
import org.ideoholic.timetable.dto.TimetableAllocationResponse;
import org.ideoholic.timetable.engine.planning.AcademicPlanningService;
import org.ideoholic.timetable.engine.planning.models.AcademicPlan;
import org.ideoholic.timetable.engine.planning.models.PlanningFilter;
import org.ideoholic.timetable.engine.planning.models.TeacherUtilization;
import org.ideoholic.timetable.engine.scoring.ScoringContext;
import org.ideoholic.timetable.engine.scoring.ScoringEngine;
import org.ideoholic.timetable.engine.scoring.ScoringResult;
import org.ideoholic.timetable.engine.strategy.GenerationStrategy;
import org.ideoholic.timetable.engine.strategy.models.GenerationContext;
import org.ideoholic.timetable.engine.strategy.models.SubjectPriority;
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

    private final GenerationStrategy generationStrategy;

    private final ScoringEngine scoringEngine;

    private final AcademicPlanningService academicPlanningService;

    @Override
    public List<TimetableAssignment> generate(
            TimetableGenerationPlan plan) {

        TeacherSubjectCatalog.Catalog catalog = teacherSubjectCatalog.build(plan.getTeachers());
        Map<Long, TeacherUtilization> teacherUtilizationByTeacherId =
                teacherUtilizationByTeacherId(plan.getSections());
        List<TimetableAssignment> generated = new ArrayList<>();

        for (WorkingDay workingDay : plan.getWorkingDays()) {
            generated.addAll(generateForWorkingDay(
                    plan.getSections(),
                    plan.getPeriods(),
                    workingDay,
                    catalog.getSubjects(),
                    catalog.getTeachersBySubjectId(),
                    teacherUtilizationByTeacherId));
        }

        return generated;
    }

    private List<TimetableAssignment> generateForWorkingDay(
            List<Section> sections,
            List<Period> periods,
            WorkingDay workingDay,
            List<Subject> availableSubjects,
            Map<Long, List<Teacher>> teachersBySubjectId,
            Map<Long, TeacherUtilization> teacherUtilizationByTeacherId) {

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
            Map<Long, Integer> samePeriodSubjectCount = new HashMap<>();

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
                samePeriodSubjectCount.clear();
                countSamePeriodSubjects(
                        previousAssignments,
                        currentDayAssignments,
                        period,
                        samePeriodSubjectCount);

                GenerationContext generationContext = generationContext(
                                section,
                                workingDay,
                                period,
                                periods.size(),
                                availableSubjects,
                                teachersBySubjectId,
                                occupiedTeacherIds,
                                weeklySubjectCount,
                                daySubjectCount,
                                samePeriodSubjectCount,
                                previousPeriodSubjectId,
                        previousDayPeriodSubjectId);

                List<SubjectPriority> candidateSubjects = generationStrategy.prioritize(
                        generationContext);

                if (candidateSubjects.isEmpty()) {
                    continue;
                }

                List<ScoringResult> scoredCandidates = scoringEngine.rank(
                        scoringContexts(
                                generationContext,
                                candidateSubjects,
                                teachersBySubjectId,
                                occupiedTeacherIds,
                                teacherUsageCount,
                                teacherUtilizationByTeacherId));

                boolean assigned = tryScoredCandidates(
                        scoredCandidates,
                        section,
                        workingDay,
                        period,
                        periodId,
                        occupiedTeacherIds,
                        teachersUsedInSection,
                        teacherUsageCount,
                        weeklySubjectCount,
                        daySubjectCount,
                        generated);

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

    private boolean tryScoredCandidates(
            List<ScoringResult> scoredCandidates,
            Section section,
            WorkingDay workingDay,
            Period period,
            Long periodId,
            Set<Long> occupiedTeacherIds,
            Set<Long> teachersUsedInSection,
            Map<Long, Integer> teacherUsageCount,
            Map<Long, Integer> weeklySubjectCount,
            Map<Long, Integer> daySubjectCount,
            List<TimetableAssignment> generated) {

        for (ScoringResult scoredCandidate : scoredCandidates) {
            Subject subject = scoredCandidate.getSubject();
            Teacher teacher = scoredCandidate.getTeacher();

            TimetableAllocationRequest allocationRequest = new TimetableAllocationRequest();
            allocationRequest.setTeacherId(teacher.getId());
            allocationRequest.setSubjectId(subject.getId());
            allocationRequest.setSectionId(section.getId());
            allocationRequest.setWorkingDayId(workingDay.getId());
            allocationRequest.setPeriodId(periodId);

            TimetableAllocationResponse response = allocationService.allocate(allocationRequest);

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
                    return true;
                }
            }
        }

        return false;
    }

    private List<ScoringContext> scoringContexts(
            GenerationContext generationContext,
            List<SubjectPriority> candidateSubjects,
            Map<Long, List<Teacher>> teachersBySubjectId,
            Set<Long> occupiedTeacherIds,
            Map<Long, Integer> teacherUsageCount,
            Map<Long, TeacherUtilization> teacherUtilizationByTeacherId) {

        Map<Long, String> categoryBySubjectId = new HashMap<>();
        candidateSubjects.stream()
                .filter(candidate -> candidate.getSubject() != null)
                .filter(candidate -> candidate.getSubject().getId() != null)
                .forEach(candidate -> categoryBySubjectId.put(
                        candidate.getSubject().getId(),
                        candidate.getCategoryName()));

        List<ScoringContext> contexts = new ArrayList<>();
        for (SubjectPriority subjectCandidate : candidateSubjects) {
            Subject subject = subjectCandidate.getSubject();
            if (subject == null || subject.getId() == null) {
                continue;
            }

            List<Teacher> eligibleTeachers = teachersBySubjectId
                    .getOrDefault(subject.getId(), new ArrayList<>())
                    .stream()
                    .filter(teacher -> teacher.getId() != null)
                    .filter(teacher -> !occupiedTeacherIds.contains(teacher.getId()))
                    .collect(Collectors.toList());

            for (Teacher teacher : eligibleTeachers) {
                ScoringContext context = new ScoringContext();
                context.setGenerationContext(generationContext);
                context.setSubjectPriority(subjectCandidate);
                context.setTeacher(teacher);
                context.setTeacherUsageCount(teacherUsageCount);
                context.setTeacherUtilizationByTeacherId(teacherUtilizationByTeacherId);
                context.setCategoryBySubjectId(categoryBySubjectId);
                contexts.add(context);
            }
        }

        return contexts;
    }

    private Map<Long, TeacherUtilization> teacherUtilizationByTeacherId(
            List<Section> sections) {

        PlanningFilter filter = new PlanningFilter();
        filter.setAcademicYearId(firstAcademicYearId(sections));
        filter.setClassIds(classIds(sections));

        AcademicPlan academicPlan = academicPlanningService.plan(filter);
        return academicPlan.getTeacherUtilizations()
                .stream()
                .filter(utilization -> utilization.getTeacherId() != null)
                .collect(Collectors.toMap(
                        TeacherUtilization::getTeacherId,
                        utilization -> utilization,
                        (left, right) -> left));
    }

    private Long firstAcademicYearId(
            List<Section> sections) {

        return sections.stream()
                .filter(section -> section.getAcademicYear() != null)
                .map(section -> section.getAcademicYear().getId())
                .filter(id -> id != null)
                .findFirst()
                .orElse(null);
    }

    private List<Long> classIds(
            List<Section> sections) {

        return sections.stream()
                .filter(section -> section.getClassMaster() != null)
                .map(section -> section.getClassMaster().getId())
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
    }

    private GenerationContext generationContext(
            Section section,
            WorkingDay workingDay,
            Period period,
            int periodsInDay,
            List<Subject> availableSubjects,
            Map<Long, List<Teacher>> teachersBySubjectId,
            Set<Long> occupiedTeacherIds,
            Map<Long, Integer> weeklySubjectCount,
            Map<Long, Integer> daySubjectCount,
            Map<Long, Integer> samePeriodSubjectCount,
            Long previousPeriodSubjectId,
            Long previousDayPeriodSubjectId) {

        GenerationContext context = new GenerationContext();
        context.setSection(section);
        context.setWorkingDay(workingDay);
        context.setPeriod(period);
        context.setPeriodsInDay(periodsInDay);
        context.setAvailableSubjects(availableSubjects);
        context.setTeachersBySubjectId(teachersBySubjectId);
        context.setOccupiedTeacherIds(occupiedTeacherIds);
        context.setWeeklySubjectCount(weeklySubjectCount);
        context.setDaySubjectCount(daySubjectCount);
        context.setSamePeriodSubjectCount(samePeriodSubjectCount);
        context.setPreviousPeriodSubjectId(previousPeriodSubjectId);
        context.setPreviousDayPeriodSubjectId(previousDayPeriodSubjectId);
        return context;
    }

    private void countSamePeriodSubjects(
            List<TimetableAssignment> previousAssignments,
            List<TimetableAssignment> currentDayAssignments,
            Period period,
            Map<Long, Integer> samePeriodSubjectCount) {

        countSamePeriodSubjects(previousAssignments, period, samePeriodSubjectCount);
        countSamePeriodSubjects(currentDayAssignments, period, samePeriodSubjectCount);
    }

    private void countSamePeriodSubjects(
            List<TimetableAssignment> assignments,
            Period period,
            Map<Long, Integer> samePeriodSubjectCount) {

        if (period == null || period.getPeriodNumber() == null) {
            return;
        }

        for (TimetableAssignment assignment : assignments) {
            if (assignment.getPeriod() == null
                    || assignment.getPeriod().getPeriodNumber() == null
                    || assignment.getSubject() == null
                    || assignment.getSubject().getId() == null) {
                continue;
            }

            if (period.getPeriodNumber().equals(assignment.getPeriod().getPeriodNumber())) {
                samePeriodSubjectCount.merge(
                        assignment.getSubject().getId(),
                        1,
                        Integer::sum);
            }
        }
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
