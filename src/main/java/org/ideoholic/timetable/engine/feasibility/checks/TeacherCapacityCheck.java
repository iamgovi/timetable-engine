package org.ideoholic.timetable.engine.feasibility.checks;

import org.ideoholic.timetable.engine.feasibility.FeasibilityCheckResult;
import org.ideoholic.timetable.engine.feasibility.FeasibilityContext;
import org.ideoholic.timetable.engine.feasibility.FeasibilityValidator;
import org.ideoholic.timetable.engine.planning.models.TeacherRequirement;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(70)
public class TeacherCapacityCheck implements FeasibilityValidator {

    private static final String CHECK_NAME = "Teacher Capacity";

    @Override
    public String name() {
        return CHECK_NAME;
    }

    @Override
    public FeasibilityCheckResult validate(
            FeasibilityContext context) {

        FeasibilityCheckResult result = new FeasibilityCheckResult(CHECK_NAME);

        if (context.getAcademicPlan() == null) {
            result.warning(
                    "Academic planning data could not be calculated.",
                    "Review curriculum and staffing data before generation.");
            return result;
        }

        for (TeacherRequirement requirement : context.getAcademicPlan().getTeacherRequirements()) {
            if (requirement.getAdditionalTeachersNeeded() > 0) {
                result.error(
                        requirement.getSubjectName() + " requires " + requirement.getRequiredPeriods()
                                + " periods but has only " + requirement.getAvailableCapacity()
                                + " periods of teacher capacity.",
                        "Add " + requirement.getAdditionalTeachersNeeded()
                                + " more mapped teacher(s) or increase configured capacity.");
            }
        }

        return result;
    }
}
