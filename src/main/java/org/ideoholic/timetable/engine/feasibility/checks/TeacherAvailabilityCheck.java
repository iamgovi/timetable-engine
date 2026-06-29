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
@Order(80)
public class TeacherAvailabilityCheck implements FeasibilityValidator {

    private static final String CHECK_NAME = "Teacher Availability";

    @Override
    public String name() {
        return CHECK_NAME;
    }

    @Override
    public FeasibilityCheckResult validate(
            FeasibilityContext context) {

        FeasibilityCheckResult result = new FeasibilityCheckResult(CHECK_NAME);
        Set<Long> selectedWorkingDayIds = context.getWorkingDays()
                .stream()
                .map(day -> day.getId())
                .collect(Collectors.toSet());
        Set<Long> selectedPeriodIds = context.getPeriods()
                .stream()
                .map(period -> period.getId())
                .collect(Collectors.toSet());
        Set<Long> availableTeacherIds = context.getTeacherAvailabilities()
                .stream()
                .filter(availability -> Boolean.TRUE.equals(availability.getAvailable()))
                .filter(availability -> availability.getTeacher() != null)
                .filter(availability -> availability.getWorkingDay() != null)
                .filter(availability -> availability.getPeriod() != null)
                .filter(availability -> selectedWorkingDayIds.contains(availability.getWorkingDay().getId()))
                .filter(availability -> selectedPeriodIds.contains(availability.getPeriod().getId()))
                .map(availability -> availability.getTeacher().getId())
                .collect(Collectors.toSet());

        for (Curriculum curriculum : context.getCurricula()) {
            for (CurriculumSubject subject : context.getCurriculumSubjectsByCurriculumId()
                    .getOrDefault(curriculum.getId(), java.util.Collections.emptyList())) {
                if (!isDemandSubject(subject) || subject.getSubject() == null) {
                    continue;
                }

                boolean hasUsableTeacher = context.getTeacherSubjectMappings()
                        .stream()
                        .filter(mapping -> teachesSubject(mapping, subject.getSubject().getId()))
                        .anyMatch(mapping -> mapping.getTeacher() != null
                                && availableTeacherIds.contains(mapping.getTeacher().getId()));

                if (!hasUsableTeacher) {
                    result.error(
                            "Subject " + subject.getSubject().getSubjectName()
                                    + " has no mapped teacher with usable availability in the requested slots.",
                            "Configure availability for at least one mapped teacher during selected working days and periods.");
                }
            }
        }

        return result;
    }

    private boolean teachesSubject(
            TeacherSubjectMapping mapping,
            Long subjectId) {

        return mapping.getSubject() != null
                && subjectId != null
                && subjectId.equals(mapping.getSubject().getId());
    }

    private boolean isDemandSubject(
            CurriculumSubject subject) {

        return subject != null
                && !Boolean.FALSE.equals(subject.getActive())
                && subject.getWeeklyPeriods() != null
                && subject.getWeeklyPeriods() > 0;
    }
}
