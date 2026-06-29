package org.ideoholic.timetable.engine.planning;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.ideoholic.timetable.engine.planning.models.CurriculumDemandResult;
import org.ideoholic.timetable.entity.ClassMaster;
import org.ideoholic.timetable.entity.Curriculum;
import org.ideoholic.timetable.entity.CurriculumSubject;
import org.ideoholic.timetable.entity.Subject;
import org.junit.jupiter.api.Test;

class CurriculumDemandCalculatorTest {

    private final CurriculumDemandCalculator calculator = new CurriculumDemandCalculator();

    @Test
    void calculatesCurriculumDemandAndSectionMultiplication() {
        Curriculum curriculum = curriculum(1L, 8L, "Class 8");
        CurriculumSubject maths = curriculumSubject(1L, "Mathematics", 6);
        CurriculumSubject english = curriculumSubject(2L, "English", 5);

        Map<Long, java.util.List<CurriculumSubject>> subjectsByCurriculumId = new HashMap<>();
        subjectsByCurriculumId.put(1L, Arrays.asList(maths, english));
        Map<Long, Integer> sectionCountByClassId = new HashMap<>();
        sectionCountByClassId.put(8L, 4);

        CurriculumDemandResult result = calculator.calculate(
                Collections.singletonList(curriculum),
                subjectsByCurriculumId,
                sectionCountByClassId);

        assertEquals(1, result.getClassDemands().size());
        assertEquals(11, result.getClassDemands().get(0).getWeeklyPeriodsPerSection());
        assertEquals(44, result.getClassDemands().get(0).getTotalWeeklyPeriods());
        assertEquals(24, result.getSubjectDemands().get(0).getTotalWeeklyPeriods());
    }

    @Test
    void handlesZeroWeeklyPeriods() {
        Curriculum curriculum = curriculum(1L, 10L, "Class 10");
        CurriculumSubject computerScience = curriculumSubject(3L, "Computer Science", 0);

        Map<Long, java.util.List<CurriculumSubject>> subjectsByCurriculumId = new HashMap<>();
        subjectsByCurriculumId.put(1L, Collections.singletonList(computerScience));
        Map<Long, Integer> sectionCountByClassId = new HashMap<>();
        sectionCountByClassId.put(10L, 2);

        CurriculumDemandResult result = calculator.calculate(
                Collections.singletonList(curriculum),
                subjectsByCurriculumId,
                sectionCountByClassId);

        assertEquals(0, result.getClassDemands().get(0).getTotalWeeklyPeriods());
        assertEquals(0, result.getSubjectDemands().get(0).getTotalWeeklyPeriods());
    }

    @Test
    void handlesNoCurriculum() {
        CurriculumDemandResult result = calculator.calculate(
                Collections.emptyList(),
                Collections.emptyMap(),
                Collections.emptyMap());

        assertEquals(0, result.getClassDemands().size());
        assertEquals(0, result.getSubjectDemands().size());
    }

    private Curriculum curriculum(
            Long curriculumId,
            Long classId,
            String className) {

        ClassMaster classMaster = new ClassMaster();
        classMaster.setId(classId);
        classMaster.setClassName(className);

        Curriculum curriculum = new Curriculum();
        curriculum.setId(curriculumId);
        curriculum.setClassMaster(classMaster);
        return curriculum;
    }

    private CurriculumSubject curriculumSubject(
            Long subjectId,
            String subjectName,
            Integer weeklyPeriods) {

        Subject subject = new Subject();
        subject.setId(subjectId);
        subject.setSubjectName(subjectName);

        CurriculumSubject curriculumSubject = new CurriculumSubject();
        curriculumSubject.setSubject(subject);
        curriculumSubject.setWeeklyPeriods(weeklyPeriods);
        curriculumSubject.setActive(Boolean.TRUE);
        return curriculumSubject;
    }
}
