package org.ideoholic.timetable.engine.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.ideoholic.timetable.engine.strategy.models.GenerationContext;
import org.ideoholic.timetable.engine.strategy.models.SubjectPriority;
import org.ideoholic.timetable.entity.AcademicYear;
import org.ideoholic.timetable.entity.ClassMaster;
import org.ideoholic.timetable.entity.Curriculum;
import org.ideoholic.timetable.entity.CurriculumSubject;
import org.ideoholic.timetable.entity.Period;
import org.ideoholic.timetable.entity.Section;
import org.ideoholic.timetable.entity.Subject;
import org.ideoholic.timetable.entity.SubjectCategory;
import org.ideoholic.timetable.entity.Teacher;
import org.ideoholic.timetable.repository.CurriculumRepository;
import org.ideoholic.timetable.repository.CurriculumSubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CurriculumGenerationStrategyTest {

    private CurriculumRepository curriculumRepository;

    private CurriculumSubjectRepository curriculumSubjectRepository;

    private CurriculumGenerationStrategy strategy;

    @BeforeEach
    void setUp() {
        curriculumRepository = mock(CurriculumRepository.class);
        curriculumSubjectRepository = mock(CurriculumSubjectRepository.class);
        strategy = new CurriculumGenerationStrategy(
                curriculumRepository,
                curriculumSubjectRepository,
                new RemainingDemandTracker(),
                new DailyDistributionTracker(1000, 500),
                new WeeklyDistributionTracker(125, 100),
                new SubjectPriorityCalculator(100, 80, 60, 20));
    }

    @Test
    void ordersCandidatesUsingCurriculumDemandAndCategoryPlacement() {
        Subject mathematics = subject(1L, "Mathematics", 6, 2);
        Subject english = subject(2L, "English", 5, 2);
        Subject pe = subject(3L, "Physical Education", 2, 1);
        Section section = section();
        Curriculum curriculum = curriculum(section);

        when(curriculumRepository.findByClassMasterAndAcademicYear(
                section.getClassMaster(),
                section.getAcademicYear()))
                .thenReturn(Optional.of(curriculum));
        when(curriculumSubjectRepository.findByCurriculumOrderByDisplayOrderAscIdAsc(curriculum))
                .thenReturn(Arrays.asList(
                        curriculumSubject(curriculum, mathematics, "Core", 6),
                        curriculumSubject(curriculum, english, "Language", 5),
                        curriculumSubject(curriculum, pe, "Activity", 2)));

        GenerationContext context = context(section, mathematics, english, pe);
        context.getWeeklySubjectCount().put(mathematics.getId(), 5);
        context.getWeeklySubjectCount().put(english.getId(), 4);

        List<SubjectPriority> priorities = strategy.prioritize(context);

        assertFalse(priorities.isEmpty());
        assertEquals(pe.getId(), priorities.get(0).getSubject().getId());
    }

    @Test
    void removesSubjectsThatReachedDailyLimit() {
        Subject mathematics = subject(1L, "Mathematics", 6, 2);
        Subject pe = subject(3L, "Physical Education", 2, 1);
        Section section = section();
        Curriculum curriculum = curriculum(section);

        when(curriculumRepository.findByClassMasterAndAcademicYear(
                section.getClassMaster(),
                section.getAcademicYear()))
                .thenReturn(Optional.of(curriculum));
        when(curriculumSubjectRepository.findByCurriculumOrderByDisplayOrderAscIdAsc(curriculum))
                .thenReturn(Arrays.asList(
                        curriculumSubject(curriculum, mathematics, "Core", 6),
                        curriculumSubject(curriculum, pe, "Activity", 2)));

        GenerationContext context = context(section, mathematics, pe);
        context.getDaySubjectCount().put(pe.getId(), 1);

        List<SubjectPriority> priorities = strategy.prioritize(context);

        assertEquals(1, priorities.size());
        assertEquals(mathematics.getId(), priorities.get(0).getSubject().getId());
    }

    private GenerationContext context(
            Section section,
            Subject... subjects) {

        Period period = new Period();
        period.setId(7L);
        period.setPeriodNumber(7);

        GenerationContext context = new GenerationContext();
        context.setSection(section);
        context.setPeriod(period);
        context.setPeriodsInDay(7);
        context.setAvailableSubjects(Arrays.asList(subjects));
        context.setTeachersBySubjectId(new HashMap<>());
        context.setOccupiedTeacherIds(new HashSet<>());
        context.setWeeklySubjectCount(new HashMap<>());
        context.setDaySubjectCount(new HashMap<>());
        context.setSamePeriodSubjectCount(new HashMap<>());

        for (Subject subject : subjects) {
            Teacher teacher = new Teacher();
            teacher.setId(subject.getId());
            context.getTeachersBySubjectId().put(
                    subject.getId(),
                    Collections.singletonList(teacher));
        }

        return context;
    }

    private Section section() {
        AcademicYear academicYear = new AcademicYear();
        academicYear.setId(1L);
        academicYear.setYearName("2026-27");

        ClassMaster classMaster = new ClassMaster();
        classMaster.setId(8L);
        classMaster.setClassName("Class 8");

        Section section = new Section();
        section.setId(1L);
        section.setSectionName("A");
        section.setAcademicYear(academicYear);
        section.setClassMaster(classMaster);
        return section;
    }

    private Curriculum curriculum(
            Section section) {

        Curriculum curriculum = new Curriculum();
        curriculum.setId(1L);
        curriculum.setAcademicYear(section.getAcademicYear());
        curriculum.setClassMaster(section.getClassMaster());
        curriculum.setCurriculumName("Class 8 Curriculum");
        curriculum.setActive(true);
        return curriculum;
    }

    private CurriculumSubject curriculumSubject(
            Curriculum curriculum,
            Subject subject,
            String categoryName,
            int weeklyPeriods) {

        SubjectCategory category = new SubjectCategory();
        category.setCategoryName(categoryName);

        CurriculumSubject curriculumSubject = new CurriculumSubject();
        curriculumSubject.setCurriculum(curriculum);
        curriculumSubject.setSubject(subject);
        curriculumSubject.setCategory(category);
        curriculumSubject.setWeeklyPeriods(weeklyPeriods);
        curriculumSubject.setActive(true);
        return curriculumSubject;
    }

    private Subject subject(
            Long id,
            String name,
            int weeklyPeriods,
            int dailyPeriods) {

        Subject subject = new Subject();
        subject.setId(id);
        subject.setSubjectName(name);
        subject.setWeeklyPeriods(weeklyPeriods);
        subject.setDailyPeriods(dailyPeriods);
        return subject;
    }
}
