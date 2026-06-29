package org.ideoholic.timetable.engine.feasibility.checks;

import org.ideoholic.timetable.engine.feasibility.FeasibilityCheckResult;
import org.ideoholic.timetable.engine.feasibility.FeasibilityContext;
import org.ideoholic.timetable.engine.feasibility.FeasibilityValidator;
import org.ideoholic.timetable.entity.Curriculum;
import org.ideoholic.timetable.entity.CurriculumSubject;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(50)
public class SlotCapacityCheck implements FeasibilityValidator {

    private static final String CHECK_NAME = "Slot Capacity";

    @Override
    public String name() {
        return CHECK_NAME;
    }

    @Override
    public FeasibilityCheckResult validate(
            FeasibilityContext context) {

        FeasibilityCheckResult result = new FeasibilityCheckResult(CHECK_NAME);
        int availableSlots = context.availableSlotsPerSection();

        for (Curriculum curriculum : context.getCurricula()) {
            int requiredPeriods = context.getCurriculumSubjectsByCurriculumId()
                    .getOrDefault(curriculum.getId(), java.util.Collections.emptyList())
                    .stream()
                    .filter(this::isDemandSubject)
                    .mapToInt(CurriculumSubject::getWeeklyPeriods)
                    .sum();

            if (requiredPeriods > availableSlots) {
                result.error(
                        "Class " + classLabel(curriculum) + " requires " + requiredPeriods
                                + " periods but only " + availableSlots + " timetable slots are available.",
                        "Add working days, add teaching periods, or reduce weekly curriculum demand.");
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
