package org.ideoholic.timetable.engine.planning;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ideoholic.timetable.engine.planning.models.ClassDemand;
import org.ideoholic.timetable.engine.planning.models.CurriculumDemandResult;
import org.ideoholic.timetable.engine.planning.models.SubjectDemand;
import org.ideoholic.timetable.entity.Curriculum;
import org.ideoholic.timetable.entity.CurriculumSubject;
import org.ideoholic.timetable.entity.Subject;
import org.springframework.stereotype.Component;

@Component
public class CurriculumDemandCalculator {

    public CurriculumDemandResult calculate(
            List<Curriculum> curricula,
            Map<Long, List<CurriculumSubject>> subjectsByCurriculumId,
            Map<Long, Integer> sectionCountByClassId) {

        List<ClassDemand> classDemands = new ArrayList<>();
        Map<Long, SubjectDemand> aggregateBySubjectId = new LinkedHashMap<>();

        curricula.stream()
                .sorted(Comparator.comparing(curriculum -> curriculum.getClassMaster().getId()))
                .forEach(curriculum -> {
                    Long classId = curriculum.getClassMaster().getId();
                    int sectionCount = sectionCountByClassId.getOrDefault(classId, 0);
                    List<CurriculumSubject> curriculumSubjects = subjectsByCurriculumId
                            .getOrDefault(curriculum.getId(), new ArrayList<>());

                    ClassDemand classDemand = new ClassDemand();
                    classDemand.setClassId(classId);
                    classDemand.setClassName(curriculum.getClassMaster().getClassName());
                    classDemand.setCurriculumId(curriculum.getId());
                    classDemand.setSectionCount(sectionCount);

                    int weeklyPeriodsPerSection = 0;
                    int totalWeeklyPeriods = 0;

                    curriculumSubjects.stream()
                            .filter(subject -> !Boolean.FALSE.equals(subject.getActive()))
                            .sorted(Comparator
                                    .comparing((CurriculumSubject subject) -> subject.getDisplayOrder() == null
                                            ? Integer.MAX_VALUE
                                            : subject.getDisplayOrder())
                                    .thenComparing(subject -> subject.getSubject().getId()))
                            .forEach(curriculumSubject -> {
                                SubjectDemand subjectDemand = toSubjectDemand(
                                        curriculum,
                                        curriculumSubject,
                                        sectionCount);
                                classDemand.getSubjects().add(subjectDemand);
                                mergeAggregate(aggregateBySubjectId, subjectDemand);
                            });

                    for (SubjectDemand subjectDemand : classDemand.getSubjects()) {
                        weeklyPeriodsPerSection += subjectDemand.getWeeklyPeriodsPerSection();
                        totalWeeklyPeriods += subjectDemand.getTotalWeeklyPeriods();
                    }

                    classDemand.setWeeklyPeriodsPerSection(weeklyPeriodsPerSection);
                    classDemand.setTotalWeeklyPeriods(totalWeeklyPeriods);
                    classDemands.add(classDemand);
                });

        CurriculumDemandResult result = new CurriculumDemandResult();
        result.setClassDemands(classDemands);
        result.setSubjectDemands(new ArrayList<>(aggregateBySubjectId.values()));
        return result;
    }

    private SubjectDemand toSubjectDemand(
            Curriculum curriculum,
            CurriculumSubject curriculumSubject,
            int sectionCount) {

        Subject subject = curriculumSubject.getSubject();
        int weeklyPeriods = curriculumSubject.getWeeklyPeriods() == null
                ? 0
                : curriculumSubject.getWeeklyPeriods();

        SubjectDemand demand = new SubjectDemand();
        demand.setClassId(curriculum.getClassMaster().getId());
        demand.setClassName(curriculum.getClassMaster().getClassName());
        demand.setSubjectId(subject.getId());
        demand.setSubjectName(subject.getSubjectName());
        demand.setCategoryName(curriculumSubject.getCategory() == null
                ? null
                : curriculumSubject.getCategory().getCategoryName());
        demand.setRequirementType(curriculumSubject.getRequirementType());
        demand.setOptionalSubject(curriculumSubject.getOptionalSubject());
        demand.setWeeklyPeriodsPerSection(weeklyPeriods);
        demand.setSectionCount(sectionCount);
        demand.setTotalWeeklyPeriods(weeklyPeriods * sectionCount);
        return demand;
    }

    private void mergeAggregate(
            Map<Long, SubjectDemand> aggregateBySubjectId,
            SubjectDemand subjectDemand) {

        SubjectDemand aggregate = aggregateBySubjectId.computeIfAbsent(
                subjectDemand.getSubjectId(),
                subjectId -> {
                    SubjectDemand demand = new SubjectDemand();
                    demand.setSubjectId(subjectDemand.getSubjectId());
                    demand.setSubjectName(subjectDemand.getSubjectName());
                    demand.setCategoryName(subjectDemand.getCategoryName());
                    demand.setRequirementType(subjectDemand.getRequirementType());
                    demand.setOptionalSubject(subjectDemand.getOptionalSubject());
                    return demand;
                });

        aggregate.setWeeklyPeriodsPerSection(
                aggregate.getWeeklyPeriodsPerSection()
                        + subjectDemand.getWeeklyPeriodsPerSection());
        aggregate.setSectionCount(
                aggregate.getSectionCount()
                        + subjectDemand.getSectionCount());
        aggregate.setTotalWeeklyPeriods(
                aggregate.getTotalWeeklyPeriods()
                        + subjectDemand.getTotalWeeklyPeriods());
    }
}
