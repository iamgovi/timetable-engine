package org.ideoholic.timetable.engine.feasibility;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.ideoholic.timetable.dto.FeasibilityRequest;
import org.ideoholic.timetable.engine.feasibility.checks.CurriculumCheck;
import org.ideoholic.timetable.engine.feasibility.checks.SectionConfigurationCheck;
import org.ideoholic.timetable.engine.feasibility.checks.SlotCapacityCheck;
import org.ideoholic.timetable.engine.feasibility.checks.SubjectMappingCheck;
import org.ideoholic.timetable.engine.feasibility.checks.TeacherAvailabilityCheck;
import org.ideoholic.timetable.engine.feasibility.checks.TeacherCapacityCheck;
import org.ideoholic.timetable.engine.feasibility.checks.WorkingDayConfigurationCheck;
import org.ideoholic.timetable.engine.planning.models.AcademicPlan;
import org.ideoholic.timetable.engine.planning.models.TeacherRequirement;
import org.ideoholic.timetable.entity.ClassMaster;
import org.ideoholic.timetable.entity.Curriculum;
import org.ideoholic.timetable.entity.CurriculumSubject;
import org.ideoholic.timetable.entity.Period;
import org.ideoholic.timetable.entity.Section;
import org.ideoholic.timetable.entity.Subject;
import org.ideoholic.timetable.entity.Teacher;
import org.ideoholic.timetable.entity.TeacherAvailability;
import org.ideoholic.timetable.entity.TeacherSubjectMapping;
import org.ideoholic.timetable.entity.WorkingDay;
import org.junit.jupiter.api.Test;

class FeasibilityChecksTest {

    @Test
    void slotCapacityFailsWhenCurriculumDemandExceedsAvailableSlots() {
        FeasibilityContext context = baseContext(36, 5, 7);

        FeasibilityCheckResult result = new SlotCapacityCheck().validate(context);

        assertFalse(result.getErrors().isEmpty());
        assertTrue(result.getErrors().get(0).getMessage().contains("36"));
    }

    @Test
    void curriculumCheckFailsWhenSelectedClassHasNoCurriculum() {
        FeasibilityContext context = baseContext(6, 5, 7);
        context.setCurricula(Collections.emptyList());
        context.setCurriculumSubjectsByCurriculumId(Collections.emptyMap());

        FeasibilityCheckResult result = new CurriculumCheck().validate(context);

        assertFalse(result.getErrors().isEmpty());
    }

    @Test
    void sectionConfigurationFailsWhenClassHasNoSections() {
        FeasibilityContext context = baseContext(6, 5, 7);
        context.setSections(Collections.emptyList());

        FeasibilityCheckResult result = new SectionConfigurationCheck().validate(context);

        assertFalse(result.getErrors().isEmpty());
    }

    @Test
    void workingDayConfigurationFailsWithoutWorkingDays() {
        FeasibilityContext context = baseContext(6, 0, 7);

        FeasibilityCheckResult result = new WorkingDayConfigurationCheck().validate(context);

        assertFalse(result.getErrors().isEmpty());
    }

    @Test
    void subjectMappingFailsWhenDemandedSubjectHasNoMappedTeacher() {
        FeasibilityContext context = baseContext(6, 5, 7);
        context.setTeacherSubjectMappings(Collections.emptyList());

        FeasibilityCheckResult result = new SubjectMappingCheck().validate(context);

        assertFalse(result.getErrors().isEmpty());
    }

    @Test
    void teacherCapacityFailsWhenPlanningReportsShortage() {
        FeasibilityContext context = baseContext(6, 5, 7);
        TeacherRequirement requirement = new TeacherRequirement();
        requirement.setSubjectName("Mathematics");
        requirement.setRequiredPeriods(54);
        requirement.setAvailableCapacity(30);
        requirement.setAdditionalTeachersNeeded(1);
        AcademicPlan plan = new AcademicPlan();
        plan.setTeacherRequirements(Collections.singletonList(requirement));
        context.setAcademicPlan(plan);

        FeasibilityCheckResult result = new TeacherCapacityCheck().validate(context);

        assertFalse(result.getErrors().isEmpty());
    }

    @Test
    void teacherAvailabilityFailsWhenMappedTeacherHasNoUsableAvailability() {
        FeasibilityContext context = baseContext(6, 5, 7);
        context.setTeacherAvailabilities(Collections.emptyList());

        FeasibilityCheckResult result = new TeacherAvailabilityCheck().validate(context);

        assertFalse(result.getErrors().isEmpty());
    }

    @Test
    void checksPassForCompleteFeasibleContext() {
        FeasibilityContext context = baseContext(6, 5, 7);

        assertTrue(new WorkingDayConfigurationCheck().validate(context).getErrors().isEmpty());
        assertTrue(new SectionConfigurationCheck().validate(context).getErrors().isEmpty());
        assertTrue(new CurriculumCheck().validate(context).getErrors().isEmpty());
        assertTrue(new SlotCapacityCheck().validate(context).getErrors().isEmpty());
        assertTrue(new SubjectMappingCheck().validate(context).getErrors().isEmpty());
        assertTrue(new TeacherCapacityCheck().validate(context).getErrors().isEmpty());
        assertTrue(new TeacherAvailabilityCheck().validate(context).getErrors().isEmpty());
    }

    private FeasibilityContext baseContext(
            int weeklyPeriods,
            int workingDayCount,
            int periodCount) {

        ClassMaster classMaster = classMaster(8L, "Class 8");
        Subject subject = subject(1L, "Mathematics", true);
        Curriculum curriculum = curriculum(10L, classMaster);
        CurriculumSubject curriculumSubject = curriculumSubject(curriculum, subject, weeklyPeriods);
        Teacher teacher = teacher(1L, "Teacher A");

        FeasibilityContext context = new FeasibilityContext();
        FeasibilityRequest request = new FeasibilityRequest();
        request.setClassIds(Collections.singletonList(classMaster.getId()));
        context.setRequest(request);
        context.setClasses(Collections.singletonList(classMaster));
        context.setSections(Collections.singletonList(section(1L, classMaster)));
        context.setWorkingDays(workingDays(workingDayCount));
        context.setPeriods(periods(periodCount));
        context.setCurricula(Collections.singletonList(curriculum));
        context.setCurriculumSubjectsByCurriculumId(Collections.singletonMap(
                curriculum.getId(),
                Collections.singletonList(curriculumSubject)));
        context.setTeacherSubjectMappings(Collections.singletonList(mapping(teacher, subject)));
        context.setTeacherAvailabilities(Collections.singletonList(availability(
                teacher,
                context.getWorkingDays().isEmpty() ? workingDay(99L) : context.getWorkingDays().get(0),
                context.getPeriods().isEmpty() ? period(99L, 99) : context.getPeriods().get(0))));
        context.setAcademicPlan(new AcademicPlan());
        return context;
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
            ClassMaster classMaster) {

        Section section = new Section();
        section.setId(id);
        section.setSectionName("A");
        section.setClassMaster(classMaster);
        return section;
    }

    private Curriculum curriculum(
            Long id,
            ClassMaster classMaster) {

        Curriculum curriculum = new Curriculum();
        curriculum.setId(id);
        curriculum.setClassMaster(classMaster);
        curriculum.setActive(true);
        return curriculum;
    }

    private CurriculumSubject curriculumSubject(
            Curriculum curriculum,
            Subject subject,
            int weeklyPeriods) {

        CurriculumSubject curriculumSubject = new CurriculumSubject();
        curriculumSubject.setCurriculum(curriculum);
        curriculumSubject.setSubject(subject);
        curriculumSubject.setWeeklyPeriods(weeklyPeriods);
        curriculumSubject.setActive(true);
        return curriculumSubject;
    }

    private Subject subject(
            Long id,
            String name,
            boolean active) {

        Subject subject = new Subject();
        subject.setId(id);
        subject.setSubjectName(name);
        subject.setActive(active);
        return subject;
    }

    private Teacher teacher(
            Long id,
            String name) {

        Teacher teacher = new Teacher();
        teacher.setId(id);
        teacher.setTeacherName(name);
        return teacher;
    }

    private TeacherSubjectMapping mapping(
            Teacher teacher,
            Subject subject) {

        TeacherSubjectMapping mapping = new TeacherSubjectMapping();
        mapping.setTeacher(teacher);
        mapping.setSubject(subject);
        return mapping;
    }

    private TeacherAvailability availability(
            Teacher teacher,
            WorkingDay day,
            Period period) {

        TeacherAvailability availability = new TeacherAvailability();
        availability.setTeacher(teacher);
        availability.setWorkingDay(day);
        availability.setPeriod(period);
        availability.setAvailable(true);
        return availability;
    }

    private java.util.List<WorkingDay> workingDays(
            int count) {

        java.util.List<WorkingDay> days = new java.util.ArrayList<>();
        for (int i = 1; i <= count; i++) {
            days.add(workingDay((long) i));
        }
        return days;
    }

    private WorkingDay workingDay(
            Long id) {

        WorkingDay day = new WorkingDay();
        day.setId(id);
        day.setDayName("Day " + id);
        day.setWorking(true);
        return day;
    }

    private java.util.List<Period> periods(
            int count) {

        java.util.List<Period> periods = new java.util.ArrayList<>();
        for (int i = 1; i <= count; i++) {
            periods.add(period((long) i, i));
        }
        return periods;
    }

    private Period period(
            Long id,
            int number) {

        Period period = new Period();
        period.setId(id);
        period.setPeriodNumber(number);
        period.setBreakPeriod(false);
        return period;
    }
}
