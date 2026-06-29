package org.ideoholic.timetable.engine.feasibility.checks;

import java.util.Set;
import java.util.stream.Collectors;

import org.ideoholic.timetable.engine.feasibility.FeasibilityCheckResult;
import org.ideoholic.timetable.engine.feasibility.FeasibilityContext;
import org.ideoholic.timetable.engine.feasibility.FeasibilityValidator;
import org.ideoholic.timetable.entity.Curriculum;
import org.ideoholic.timetable.entity.CurriculumSubject;
import org.ideoholic.timetable.entity.TeacherSubjectMapping;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(60)
public class SubjectMappingCheck implements FeasibilityValidator {

    private static final String CHECK_NAME = "Teacher Mapping";

    @Override
    public String name() {
        return CHECK_NAME;
    }

    @Override
    public FeasibilityCheckResult validate(
            FeasibilityContext context) {

        FeasibilityCheckResult result = new FeasibilityCheckResult(CHECK_NAME);
        Set<Long> mappedSubjectIds = context.getTeacherSubjectMappings()
                .stream()
                .filter(mapping -> mapping.getSubject() != null)
                .map(mapping -> mapping.getSubject().getId())
                .collect(Collectors.toSet());

        for (Curriculum curriculum : context.getCurricula()) {
            for (CurriculumSubject subject : context.getCurriculumSubjectsByCurriculumId()
                    .getOrDefault(curriculum.getId(), java.util.Collections.emptyList())) {
                if (!isDemandSubject(subject) || subject.getSubject() == null) {
                    continue;
                }

                if (!mappedSubjectIds.contains(subject.getSubject().getId())) {
                    result.error(
                            "No teacher is mapped to subject " + subject.getSubject().getSubjectName()
                                    + " for class " + classLabel(curriculum) + ".",
                            "Create at least one teacher-subject mapping for every demanded subject.");
                }
            }
        }

        return result;
    }

    private boolean isDemandSubject(
            CurriculumSubject subject) {

        return subject != null
                && !Boolean.FALSE.equals(subject.getActive())
                && subject.getWeeklyPeriods() != null
                && subject.getWeeklyPeriods() > 0;
    }

    private String classLabel(
            Curriculum curriculum) {

        if (curriculum == null || curriculum.getClassMaster() == null) {
            return "unknown";
        }
        if (curriculum.getClassMaster().getClassName() != null
                && !curriculum.getClassMaster().getClassName().isBlank()) {
            return curriculum.getClassMaster().getClassName();
        }
        return String.valueOf(curriculum.getClassMaster().getId());
    }
}
