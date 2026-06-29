package org.ideoholic.timetable.engine.planning;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.ideoholic.timetable.engine.planning.models.SubjectDemand;
import org.ideoholic.timetable.engine.planning.models.TeacherCapacity;
import org.ideoholic.timetable.engine.planning.models.TeacherRequirement;
import org.ideoholic.timetable.engine.planning.models.TeacherUtilization;
import org.ideoholic.timetable.entity.Subject;
import org.ideoholic.timetable.entity.Teacher;
import org.ideoholic.timetable.entity.TeacherSubjectMapping;
import org.junit.jupiter.api.Test;

class TeacherPlanningCalculatorTest {

    @Test
    void calculatesTeacherRequirementShortage() {
        TeacherRequirementAnalyzer analyzer = new TeacherRequirementAnalyzer(30);
        SubjectDemand mathsDemand = subjectDemand(1L, "Mathematics", 54);
        TeacherCapacity capacity = teacherCapacity(1L, "Teacher A", 30);
        TeacherSubjectMapping mapping = mapping(1L, "Teacher A", 1L, "Mathematics");

        List<TeacherRequirement> requirements = analyzer.analyze(
                Collections.singletonList(mathsDemand),
                Collections.singletonList(capacity),
                Collections.singletonList(mapping));

        TeacherRequirement requirement = requirements.get(0);
        assertEquals(54, requirement.getRequiredPeriods());
        assertEquals(1, requirement.getAssignedTeachers());
        assertEquals(2, requirement.getRequiredTeachers());
        assertEquals(1, requirement.getAdditionalTeachersNeeded());
        assertEquals("SHORTAGE", requirement.getStatus());
    }

    @Test
    void calculatesRequirementWhenNoTeachersExist() {
        TeacherRequirementAnalyzer analyzer = new TeacherRequirementAnalyzer(30);

        List<TeacherRequirement> requirements = analyzer.analyze(
                Collections.singletonList(subjectDemand(1L, "Mathematics", 24)),
                Collections.emptyList(),
                Collections.emptyList());

        assertEquals(1, requirements.get(0).getRequiredTeachers());
        assertEquals(1, requirements.get(0).getAdditionalTeachersNeeded());
        assertEquals("SHORTAGE", requirements.get(0).getStatus());
    }

    @Test
    void calculatesTeacherUtilization() {
        TeacherUtilizationCalculator calculator = new TeacherUtilizationCalculator();

        List<TeacherUtilization> utilizations = calculator.calculate(
                Arrays.asList(
                        subjectDemand(1L, "Mathematics", 24),
                        subjectDemand(2L, "English", 6)),
                Collections.singletonList(teacherCapacity(1L, "Teacher A", 30)),
                Arrays.asList(
                        mapping(1L, "Teacher A", 1L, "Mathematics"),
                        mapping(1L, "Teacher A", 2L, "English")));

        TeacherUtilization utilization = utilizations.get(0);
        assertEquals(30, utilization.getAssignedCurriculumLoad());
        assertEquals(100.0, utilization.getUtilizationPercent());
        assertFalse(utilization.isOverCapacity());
        assertFalse(utilization.isUnderUtilized());
    }

    @Test
    void flagsOverCapacityAndUnderUtilizedTeachers() {
        TeacherUtilizationCalculator calculator = new TeacherUtilizationCalculator();

        List<TeacherUtilization> utilizations = calculator.calculate(
                Arrays.asList(
                        subjectDemand(1L, "Mathematics", 31),
                        subjectDemand(2L, "English", 10)),
                Arrays.asList(
                        teacherCapacity(1L, "Teacher A", 30),
                        teacherCapacity(2L, "Teacher B", 30)),
                Arrays.asList(
                        mapping(1L, "Teacher A", 1L, "Mathematics"),
                        mapping(2L, "Teacher B", 2L, "English")));

        assertTrue(utilizations.get(0).isOverCapacity());
        assertTrue(utilizations.get(1).isUnderUtilized());
    }

    private SubjectDemand subjectDemand(
            Long subjectId,
            String subjectName,
            int totalWeeklyPeriods) {

        SubjectDemand demand = new SubjectDemand();
        demand.setSubjectId(subjectId);
        demand.setSubjectName(subjectName);
        demand.setTotalWeeklyPeriods(totalWeeklyPeriods);
        return demand;
    }

    private TeacherCapacity teacherCapacity(
            Long teacherId,
            String teacherName,
            int maxWeeklyPeriods) {

        TeacherCapacity capacity = new TeacherCapacity();
        capacity.setTeacherId(teacherId);
        capacity.setTeacherName(teacherName);
        capacity.setMaxWeeklyPeriods(maxWeeklyPeriods);
        return capacity;
    }

    private TeacherSubjectMapping mapping(
            Long teacherId,
            String teacherName,
            Long subjectId,
            String subjectName) {

        Teacher teacher = new Teacher();
        teacher.setId(teacherId);
        teacher.setTeacherName(teacherName);
        teacher.setActive(Boolean.TRUE);

        Subject subject = new Subject();
        subject.setId(subjectId);
        subject.setSubjectName(subjectName);

        TeacherSubjectMapping mapping = new TeacherSubjectMapping();
        mapping.setTeacher(teacher);
        mapping.setSubject(subject);
        return mapping;
    }
}
