package org.ideoholic.timetable.engine.scheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ideoholic.timetable.engine.feasibility.FeasibilityReport;
import org.ideoholic.timetable.engine.planning.AcademicPlanningService;
import org.ideoholic.timetable.engine.planning.models.AcademicPlan;
import org.ideoholic.timetable.engine.planning.models.PlanningFilter;
import org.ideoholic.timetable.entity.AcademicYear;
import org.ideoholic.timetable.entity.ClassMaster;
import org.ideoholic.timetable.entity.Section;
import org.ideoholic.timetable.entity.WorkingDay;
import org.ideoholic.timetable.repository.AcademicYearRepository;
import org.ideoholic.timetable.repository.ClassMasterRepository;
import org.ideoholic.timetable.repository.CurriculumRepository;
import org.ideoholic.timetable.repository.SectionRepository;
import org.ideoholic.timetable.repository.WorkingDayRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SchoolScheduler {

    private final AcademicYearRepository academicYearRepository;

    private final ClassMasterRepository classMasterRepository;

    private final SectionRepository sectionRepository;

    private final WorkingDayRepository workingDayRepository;

    private final CurriculumRepository curriculumRepository;

    private final AcademicPlanningService planningService;

    private final SchedulingPriorityCalculator priorityCalculator;

    private final SectionScheduler sectionScheduler;

    public SchedulerReport schedule(
            Long academicYearId,
            List<Long> classIds,
            List<Long> workingDayIds) {

        Instant startedAt = Instant.now();
        SchedulerReport report = new SchedulerReport();
        AcademicYear academicYear = resolveAcademicYear(academicYearId);
        List<ClassMaster> classes = resolveClasses(academicYear, classIds);
        List<Section> sections = resolveSections(academicYear, classes);
        List<WorkingDay> workingDays = resolveWorkingDays(workingDayIds);
        AcademicPlan plan = planningService.plan(planningFilter(academicYear, classes));
        SchedulingQueue queue = buildQueue(academicYear, classes, sections, workingDays, plan);

        while (!queue.isEmpty()) {
            SchedulingTask task = queue.next().orElse(null);
            if (task == null) {
                continue;
            }

            Object result = sectionScheduler.schedule(task);
            if (result instanceof FeasibilityReport
                    && !((FeasibilityReport) result).isFeasible()) {
                report.recordFailure(task, "Feasibility failed for " + sectionLabel(task));
            } else {
                report.recordSuccess(task, sectionScheduler.assignmentCount(result));
            }
        }

        report.setExecutionDurationMillis(Duration.between(startedAt, Instant.now()).toMillis());
        return report;
    }

    public SchedulingQueue buildQueue(
            AcademicYear academicYear,
            List<ClassMaster> classes,
            List<Section> sections,
            List<WorkingDay> workingDays,
            AcademicPlan plan) {

        Map<Long, Integer> sectionCountByClassId = sectionCountByClassId(sections);
        SchedulingQueue queue = new SchedulingQueue();

        for (Section section : sections) {
            if (section.getClassMaster() == null) {
                continue;
            }

            SchedulingTask task = new SchedulingTask(
                    academicYear,
                    section.getClassMaster(),
                    section,
                    new ArrayList<>(workingDays));
            task.setPriorityScore(priorityCalculator.calculate(task, plan, sectionCountByClassId));
            queue.add(task);
        }

        queue.order();
        return queue;
    }

    private AcademicYear resolveAcademicYear(
            Long academicYearId) {

        if (academicYearId != null) {
            return academicYearRepository.findById(academicYearId)
                    .orElseThrow(() -> new IllegalArgumentException("Academic year not found: " + academicYearId));
        }

        return academicYearRepository.findByCurrentTrue()
                .stream()
                .min(Comparator.comparing(AcademicYear::getId))
                .orElseGet(() -> academicYearRepository.findAll()
                        .stream()
                        .min(Comparator.comparing(AcademicYear::getId))
                        .orElseThrow(() -> new IllegalArgumentException("Academic year not found")));
    }

    private List<ClassMaster> resolveClasses(
            AcademicYear academicYear,
            List<Long> classIds) {

        if (classIds != null && !classIds.isEmpty()) {
            return classMasterRepository.findAllById(classIds)
                    .stream()
                    .sorted(Comparator.comparing(ClassMaster::getId))
                    .collect(Collectors.toList());
        }

        return curriculumRepository.findByAcademicYearId(academicYear.getId())
                .stream()
                .filter(curriculum -> !Boolean.FALSE.equals(curriculum.getActive()))
                .filter(curriculum -> curriculum.getClassMaster() != null)
                .map(curriculum -> curriculum.getClassMaster())
                .sorted(Comparator.comparing(ClassMaster::getId))
                .collect(Collectors.toList());
    }

    private List<Section> resolveSections(
            AcademicYear academicYear,
            List<ClassMaster> classes) {

        if (classes.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> classIds = classes.stream()
                .map(ClassMaster::getId)
                .collect(Collectors.toList());

        return sectionRepository.findByAcademicYearIdAndClassMasterIdIn(
                academicYear.getId(),
                classIds)
                .stream()
                .sorted(Comparator
                        .comparing((Section section) -> section.getClassMaster() == null
                                ? Long.MAX_VALUE
                                : section.getClassMaster().getId())
                        .thenComparing(section -> section.getSectionName() == null
                                ? ""
                                : section.getSectionName())
                        .thenComparing(section -> section.getId() == null
                                ? Long.MAX_VALUE
                                : section.getId()))
                .collect(Collectors.toList());
    }

    private List<WorkingDay> resolveWorkingDays(
            List<Long> workingDayIds) {

        if (workingDayIds != null && !workingDayIds.isEmpty()) {
            return workingDayRepository.findAllById(workingDayIds)
                    .stream()
                    .filter(day -> !Boolean.FALSE.equals(day.getWorking()))
                    .sorted(Comparator.comparing(WorkingDay::getId))
                    .collect(Collectors.toList());
        }

        return workingDayRepository.findAll()
                .stream()
                .filter(day -> !Boolean.FALSE.equals(day.getWorking()))
                .sorted(Comparator.comparing(WorkingDay::getId))
                .collect(Collectors.toList());
    }

    private PlanningFilter planningFilter(
            AcademicYear academicYear,
            List<ClassMaster> classes) {

        PlanningFilter filter = new PlanningFilter();
        filter.setAcademicYearId(academicYear.getId());
        filter.setClassIds(classes.stream()
                .map(ClassMaster::getId)
                .collect(Collectors.toList()));
        return filter;
    }

    private Map<Long, Integer> sectionCountByClassId(
            List<Section> sections) {

        Map<Long, Integer> sectionCountByClassId = new LinkedHashMap<>();
        for (Section section : sections) {
            if (section.getClassMaster() != null && section.getClassMaster().getId() != null) {
                sectionCountByClassId.merge(section.getClassMaster().getId(), 1, Integer::sum);
            }
        }
        return sectionCountByClassId;
    }

    private String sectionLabel(
            SchedulingTask task) {

        return task.getClassMaster().getClassName() + " - " + task.getSection().getSectionName();
    }
}
