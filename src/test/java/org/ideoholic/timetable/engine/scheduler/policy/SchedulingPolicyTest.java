package org.ideoholic.timetable.engine.scheduler.policy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.ideoholic.timetable.engine.planning.models.AcademicPlan;
import org.ideoholic.timetable.engine.planning.models.ClassDemand;
import org.ideoholic.timetable.engine.planning.models.SubjectDemand;
import org.ideoholic.timetable.engine.planning.models.TeacherRequirement;
import org.ideoholic.timetable.engine.planning.models.TeacherUtilization;
import org.ideoholic.timetable.engine.scheduler.SchedulingTask;
import org.ideoholic.timetable.entity.ClassMaster;
import org.ideoholic.timetable.entity.Section;
import org.junit.jupiter.api.Test;

class SchedulingPolicyTest {

    @Test
    void balancedPolicyOrdersByCombinedDemandSectionsAndScarcity() {
        SchedulingTask classEight = task(8L, "Class 8", 1L, "A");
        SchedulingTask classTen = task(10L, "Class 10", 2L, "A");
        AcademicPlan plan = plan();
        classDemand(plan, 8L, 30);
        classDemand(plan, 10L, 28);
        subjectDemand(plan, 8L, 1L, "Mathematics");
        subjectDemand(plan, 10L, 2L, "English");
        teacherRequirement(plan, 1L, 1);
        teacherRequirement(plan, 2L, 0);

        List<SchedulingTask> prioritized = new BalancedSchedulingPolicy().prioritize(
                Arrays.asList(classTen, classEight),
                plan);

        assertEquals(8L, prioritized.get(0).getClassMaster().getId());
        assertEquals(3060, prioritized.get(0).getPriorityScore());
    }

    @Test
    void curriculumPolicyOrdersPrimarilyByCurriculumDemand() {
        SchedulingTask lowDemand = task(8L, "Class 8", 1L, "A");
        SchedulingTask highDemand = task(10L, "Class 10", 2L, "A");
        AcademicPlan plan = plan();
        classDemand(plan, 8L, 25);
        classDemand(plan, 10L, 36);

        List<SchedulingTask> prioritized = new CurriculumPriorityPolicy().prioritize(
                Arrays.asList(lowDemand, highDemand),
                plan);

        assertEquals(10L, prioritized.get(0).getClassMaster().getId());
        assertEquals(3600, prioritized.get(0).getPriorityScore());
    }

    @Test
    void teacherScarcityPolicyOrdersByShortageAndUtilization() {
        SchedulingTask lowerScarcity = task(8L, "Class 8", 1L, "A");
        SchedulingTask higherScarcity = task(10L, "Class 10", 2L, "A");
        AcademicPlan plan = plan();
        subjectDemand(plan, 8L, 1L, "Mathematics");
        subjectDemand(plan, 10L, 2L, "Physics");
        teacherRequirement(plan, 1L, 0);
        teacherRequirement(plan, 2L, 2);
        teacherUtilization(plan, "Mathematics", 70.0);
        teacherUtilization(plan, "Physics", 95.0);

        List<SchedulingTask> prioritized = new TeacherScarcityPolicy().prioritize(
                Arrays.asList(lowerScarcity, higherScarcity),
                plan);

        assertEquals(10L, prioritized.get(0).getClassMaster().getId());
        assertEquals(295, prioritized.get(0).getPriorityScore());
    }

    @Test
    void factoryReturnsConfiguredPolicy() {
        SchedulingPolicy policy = new SchedulingPolicyFactory(
                Arrays.asList(
                        new BalancedSchedulingPolicy(),
                        new CurriculumPriorityPolicy(),
                        new TeacherScarcityPolicy()),
                "teacher-scarcity")
                .activePolicy();

        assertEquals("teacher-scarcity", policy.name());
    }

    @Test
    void factoryDefaultsToBalancedForUnknownPolicy() {
        SchedulingPolicy policy = new SchedulingPolicyFactory(
                Arrays.asList(
                        new BalancedSchedulingPolicy(),
                        new CurriculumPriorityPolicy()),
                "unknown")
                .activePolicy();

        assertEquals("balanced", policy.name());
    }

    private AcademicPlan plan() {
        return new AcademicPlan();
    }

    private void classDemand(
            AcademicPlan plan,
            Long classId,
            int weeklyPeriods) {

        ClassDemand demand = new ClassDemand();
        demand.setClassId(classId);
        demand.setWeeklyPeriodsPerSection(weeklyPeriods);
        plan.getClassDemands().add(demand);
    }

    private void subjectDemand(
            AcademicPlan plan,
            Long classId,
            Long subjectId,
            String subjectName) {

        SubjectDemand demand = new SubjectDemand();
        demand.setClassId(classId);
        demand.setSubjectId(subjectId);
        demand.setSubjectName(subjectName);
        plan.getSubjectDemands().add(demand);
    }

    private void teacherRequirement(
            AcademicPlan plan,
            Long subjectId,
            int additionalTeachersNeeded) {

        TeacherRequirement requirement = new TeacherRequirement();
        requirement.setSubjectId(subjectId);
        requirement.setAdditionalTeachersNeeded(additionalTeachersNeeded);
        plan.getTeacherRequirements().add(requirement);
    }

    private void teacherUtilization(
            AcademicPlan plan,
            String subjectName,
            double utilizationPercent) {

        TeacherUtilization utilization = new TeacherUtilization();
        utilization.setSubjects(Collections.singletonList(subjectName));
        utilization.setUtilizationPercent(utilizationPercent);
        plan.getTeacherUtilizations().add(utilization);
    }

    private SchedulingTask task(
            Long classId,
            String className,
            Long sectionId,
            String sectionName) {

        ClassMaster classMaster = new ClassMaster();
        classMaster.setId(classId);
        classMaster.setClassName(className);

        Section section = new Section();
        section.setId(sectionId);
        section.setSectionName(sectionName);
        section.setClassMaster(classMaster);

        SchedulingTask task = new SchedulingTask();
        task.setClassMaster(classMaster);
        task.setSection(section);
        return task;
    }
}
