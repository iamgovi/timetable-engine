package org.ideoholic.timetable.engine.feasibility.checks;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.ideoholic.timetable.engine.feasibility.FeasibilityCheckResult;
import org.ideoholic.timetable.engine.feasibility.FeasibilityContext;
import org.ideoholic.timetable.engine.feasibility.FeasibilityValidator;
import org.ideoholic.timetable.entity.ClassMaster;
import org.ideoholic.timetable.entity.Curriculum;
import org.ideoholic.timetable.entity.CurriculumSubject;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(30)
public class CurriculumCheck implements FeasibilityValidator {

    private static final String CHECK_NAME = "Curriculum Completeness";

    @Override
    public String name() {
        return CHECK_NAME;
    }

    @Override
    public FeasibilityCheckResult validate(
            FeasibilityContext context) {

        FeasibilityCheckResult result = new FeasibilityCheckResult(CHECK_NAME);

        Map<Long, Curriculum> curriculumByClassId = context.getCurricula()
                .stream()
                .filter(curriculum -> curriculum.getClassMaster() != null)
                .collect(Collectors.toMap(
                        curriculum -> curriculum.getClassMaster().getId(),
                        curriculum -> curriculum,
                        (first, second) -> first));

        Set<Long> requestedClassIds = context.getClasses()
                .stream()
                .map(ClassMaster::getId)
                .collect(Collectors.toSet());

        for (ClassMaster classMaster : context.getClasses()) {
            Curriculum curriculum = curriculumByClassId.get(classMaster.getId());
            if (curriculum == null) {
                result.error(
                        "Class " + classLabel(classMaster) + " has no active curriculum.",
                        "Create and activate a curriculum for every selected class.");
                continue;
            }

            java.util.List<CurriculumSubject> subjects = context.getCurriculumSubjectsByCurriculumId()
                    .getOrDefault(curriculum.getId(), java.util.Collections.emptyList());
            if (subjects.isEmpty()) {
                result.error(
                        "Curriculum for class " + classLabel(classMaster) + " has no subjects.",
                        "Add curriculum subjects before running generation.");
            }
        }

        for (Curriculum curriculum : context.getCurricula()) {
            if (curriculum.getClassMaster() == null
                    || !requestedClassIds.contains(curriculum.getClassMaster().getId())) {
                continue;
            }

            for (CurriculumSubject subject : context.getCurriculumSubjectsByCurriculumId()
                    .getOrDefault(curriculum.getId(), java.util.Collections.emptyList())) {
                if (subject.getWeeklyPeriods() == null) {
                    result.error(
                            "A curriculum subject in class " + classLabel(curriculum.getClassMaster())
                                    + " has no weekly period requirement.",
                            "Set weeklyPeriods for every curriculum subject.");
                } else if (subject.getWeeklyPeriods() < 0) {
                    result.error(
                            "A curriculum subject in class " + classLabel(curriculum.getClassMaster())
                                    + " has negative weekly periods.",
                            "Weekly period requirements must be zero or greater.");
                } else if (subject.getWeeklyPeriods() == 0 && Boolean.TRUE.equals(subject.getActive())) {
                    result.warning(
                            "Curriculum subject " + subjectLabel(subject) + " in class "
                                    + classLabel(curriculum.getClassMaster())
                                    + " is active with zero weekly periods.",
                            "Keep zero-period subjects only for optional future planning.");
                }
            }
        }

        return result;
    }

    private String classLabel(
            ClassMaster classMaster) {

        if (classMaster == null) {
            return "unknown";
        }
        if (classMaster.getClassName() != null && !classMaster.getClassName().isBlank()) {
            return classMaster.getClassName();
        }
        return String.valueOf(classMaster.getId());
    }

    private String subjectLabel(
            CurriculumSubject subject) {

        if (subject == null || subject.getSubject() == null) {
            return "unknown subject";
        }
        return subject.getSubject().getSubjectName();
    }
}
