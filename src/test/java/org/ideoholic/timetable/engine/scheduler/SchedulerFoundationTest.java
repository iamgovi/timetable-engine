package org.ideoholic.timetable.engine.scheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.ideoholic.timetable.dto.SimpleTimetableGenerationRequest;
import org.ideoholic.timetable.engine.planning.AcademicPlanningService;
import org.ideoholic.timetable.engine.planning.models.AcademicPlan;
import org.ideoholic.timetable.engine.planning.models.ClassDemand;
import org.ideoholic.timetable.engine.planning.models.PlanningFilter;
import org.ideoholic.timetable.engine.planning.models.SubjectDemand;
import org.ideoholic.timetable.engine.planning.models.TeacherRequirement;
import org.ideoholic.timetable.entity.AcademicYear;
import org.ideoholic.timetable.entity.ClassMaster;
import org.ideoholic.timetable.entity.Section;
import org.ideoholic.timetable.entity.TimetableAssignment;
import org.ideoholic.timetable.entity.WorkingDay;
import org.ideoholic.timetable.repository.AcademicYearRepository;
import org.ideoholic.timetable.repository.ClassMasterRepository;
import org.ideoholic.timetable.repository.CurriculumRepository;
import org.ideoholic.timetable.repository.SectionRepository;
import org.ideoholic.timetable.repository.WorkingDayRepository;
import org.ideoholic.timetable.service.TimetableGenerationService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class SchedulerFoundationTest {

    @Test
    void queueOrdersTasksByPriorityThenClassAndSection() {
        SchedulingTask low = task(classMaster(8L, "Class 8"), section(2L, "B", 8L), 100);
        SchedulingTask highB = task(classMaster(10L, "Class 10"), section(4L, "B", 10L), 200);
        SchedulingTask highA = task(classMaster(10L, "Class 10"), section(3L, "A", 10L), 200);

        SchedulingQueue queue = new SchedulingQueue(Arrays.asList(low, highB, highA));
        queue.order();

        assertEquals("A", queue.next().get().getSection().getSectionName());
        assertEquals("B", queue.next().get().getSection().getSectionName());
        assertEquals(8L, queue.next().get().getClassMaster().getId());
        assertFalse(queue.next().isPresent());
    }

    @Test
    void priorityCalculatorUsesCurriculumDemandSectionsAndTeacherScarcity() {
        SchedulingPriorityCalculator calculator = new SchedulingPriorityCalculator();
        AcademicPlan plan = new AcademicPlan();
        ClassDemand classDemand = new ClassDemand();
        classDemand.setClassId(8L);
        classDemand.setWeeklyPeriodsPerSection(30);
        SubjectDemand subjectDemand = new SubjectDemand();
        subjectDemand.setClassId(8L);
        subjectDemand.setSubjectId(1L);
        TeacherRequirement teacherRequirement = new TeacherRequirement();
        teacherRequirement.setSubjectId(1L);
        teacherRequirement.setAdditionalTeachersNeeded(2);
        plan.setClassDemands(Collections.singletonList(classDemand));
        plan.setSubjectDemands(Collections.singletonList(subjectDemand));
        plan.setTeacherRequirements(Collections.singletonList(teacherRequirement));

        int score = calculator.calculate(
                task(classMaster(8L, "Class 8"), section(1L, "A", 8L), 0),
                plan,
                Collections.singletonMap(8L, 4));

        assertEquals(3140, score);
    }

    @Test
    void buildQueueCreatesOneTaskPerSection() {
        SchedulingPriorityCalculator priorityCalculator = mock(SchedulingPriorityCalculator.class);
        when(priorityCalculator.calculate(any(), any(), any())).thenReturn(100);
        SchoolScheduler scheduler = new SchoolScheduler(
                null,
                null,
                null,
                null,
                null,
                null,
                priorityCalculator,
                null);

        ClassMaster classEight = classMaster(8L, "Class 8");
        List<Section> sections = Arrays.asList(
                section(1L, "A", classEight),
                section(2L, "B", classEight));

        SchedulingQueue queue = scheduler.buildQueue(
                academicYear(1L),
                Collections.singletonList(classEight),
                sections,
                Collections.singletonList(workingDay(1L)),
                new AcademicPlan());

        assertEquals(2, queue.size());
        verify(priorityCalculator, times(2)).calculate(any(), any(), any());
    }

    @Test
    void schedulerIteratesThroughEveryQueuedSection() {
        AcademicYearRepository academicYearRepository = mock(AcademicYearRepository.class);
        ClassMasterRepository classMasterRepository = mock(ClassMasterRepository.class);
        SectionRepository sectionRepository = mock(SectionRepository.class);
        WorkingDayRepository workingDayRepository = mock(WorkingDayRepository.class);
        CurriculumRepository curriculumRepository = mock(CurriculumRepository.class);
        AcademicPlanningService planningService = mock(AcademicPlanningService.class);
        SchedulingPriorityCalculator priorityCalculator = mock(SchedulingPriorityCalculator.class);
        SectionScheduler sectionScheduler = mock(SectionScheduler.class);

        AcademicYear academicYear = academicYear(1L);
        ClassMaster classEight = classMaster(8L, "Class 8");
        Section sectionA = section(1L, "A", classEight);
        Section sectionB = section(2L, "B", classEight);
        WorkingDay monday = workingDay(1L);

        when(academicYearRepository.findById(1L)).thenReturn(Optional.of(academicYear));
        when(classMasterRepository.findAllById(Collections.singletonList(8L)))
                .thenReturn(Collections.singletonList(classEight));
        when(sectionRepository.findByAcademicYearIdAndClassMasterIdIn(1L, Collections.singletonList(8L)))
                .thenReturn(Arrays.asList(sectionA, sectionB));
        when(workingDayRepository.findAllById(Collections.singletonList(1L)))
                .thenReturn(Collections.singletonList(monday));
        when(planningService.plan(any(PlanningFilter.class))).thenReturn(new AcademicPlan());
        when(priorityCalculator.calculate(any(), any(), any())).thenReturn(100);
        when(sectionScheduler.schedule(any())).thenReturn(Collections.singletonList(new TimetableAssignment()));
        when(sectionScheduler.assignmentCount(any())).thenReturn(1);

        SchoolScheduler scheduler = new SchoolScheduler(
                academicYearRepository,
                classMasterRepository,
                sectionRepository,
                workingDayRepository,
                curriculumRepository,
                planningService,
                priorityCalculator,
                sectionScheduler);

        SchedulerReport report = scheduler.schedule(1L, Collections.singletonList(8L), Collections.singletonList(1L));

        assertEquals(1, report.getClassesProcessed());
        assertEquals(2, report.getSectionsProcessed());
        assertEquals(2, report.getAssignmentsGenerated());
        assertEquals(0, report.getFailedSections());
        verify(sectionScheduler, times(2)).schedule(any());
    }

    @Test
    void schedulerReportAggregatesFailuresAndWarnings() {
        SchedulerReport report = new SchedulerReport();
        SchedulingTask task = task(classMaster(8L, "Class 8"), section(1L, "A", 8L), 0);

        report.recordFailure(task, "No feasible slots");

        assertEquals(1, report.getClassesProcessed());
        assertEquals(1, report.getSectionsProcessed());
        assertEquals(1, report.getFailedSections());
        assertEquals(Collections.singletonList("No feasible slots"), report.getWarnings());
    }

    @Test
    void sectionSchedulerDelegatesToExistingSingleSectionGeneration() {
        TimetableGenerationService generationService = mock(TimetableGenerationService.class);
        SectionScheduler sectionScheduler = new SectionScheduler(generationService);
        SchedulingTask task = new SchedulingTask(
                academicYear(1L),
                classMaster(8L, "Class 8"),
                section(5L, "C", 8L),
                Arrays.asList(workingDay(1L), workingDay(2L)));

        sectionScheduler.schedule(task);

        ArgumentCaptor<SimpleTimetableGenerationRequest> requestCaptor =
                ArgumentCaptor.forClass(SimpleTimetableGenerationRequest.class);
        verify(generationService).generateSingleSectionTimetable(requestCaptor.capture());
        assertEquals(5L, requestCaptor.getValue().getSectionId());
        assertEquals(Arrays.asList(1L, 2L), requestCaptor.getValue().getWorkingDayIds());
    }

    private SchedulingTask task(
            ClassMaster classMaster,
            Section section,
            int priorityScore) {

        SchedulingTask task = new SchedulingTask();
        task.setAcademicYear(academicYear(1L));
        task.setClassMaster(classMaster);
        task.setSection(section);
        task.setWorkingDays(Collections.singletonList(workingDay(1L)));
        task.setPriorityScore(priorityScore);
        return task;
    }

    private AcademicYear academicYear(
            Long id) {

        AcademicYear academicYear = new AcademicYear();
        academicYear.setId(id);
        academicYear.setYearName("2026-27");
        return academicYear;
    }

    private ClassMaster classMaster(
            Long id,
            String name) {

        ClassMaster classMaster = new ClassMaster();
        classMaster.setId(id);
        classMaster.setClassName(name);
        return classMaster;
    }

    private Section section(
            Long id,
            String name,
            Long classId) {

        return section(id, name, classMaster(classId, "Class " + classId));
    }

    private Section section(
            Long id,
            String name,
            ClassMaster classMaster) {

        Section section = new Section();
        section.setId(id);
        section.setSectionName(name);
        section.setClassMaster(classMaster);
        return section;
    }

    private WorkingDay workingDay(
            Long id) {

        WorkingDay workingDay = new WorkingDay();
        workingDay.setId(id);
        workingDay.setDayName("Day " + id);
        workingDay.setWorking(true);
        return workingDay;
    }
}
