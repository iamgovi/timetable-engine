package org.ideoholic.timetable.engine.feasibility.checks;

import org.ideoholic.timetable.engine.feasibility.FeasibilityCheckResult;
import org.ideoholic.timetable.engine.feasibility.FeasibilityContext;
import org.ideoholic.timetable.engine.feasibility.FeasibilityValidator;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(10)
public class WorkingDayConfigurationCheck implements FeasibilityValidator {

    private static final String CHECK_NAME = "Working Day Configuration";

    @Override
    public String name() {
        return CHECK_NAME;
    }

    @Override
    public FeasibilityCheckResult validate(
            FeasibilityContext context) {

        FeasibilityCheckResult result = new FeasibilityCheckResult(CHECK_NAME);

        if (context.getWorkingDays().isEmpty()) {
            result.error(
                    "No working days are available for the requested timetable.",
                    "Configure active working days or pass valid workingDayIds.");
        }

        if (context.getPeriods().isEmpty()) {
            result.error(
                    "No teaching periods are configured.",
                    "Configure non-break periods before generation.");
        }

        if (context.availableSlotsPerSection() == 0) {
            result.error(
                    "The requested timetable has zero available teaching slots.",
                    "Select working days and ensure periods are configured as teaching periods.");
        }

        return result;
    }
}
