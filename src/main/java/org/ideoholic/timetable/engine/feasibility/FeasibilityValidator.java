package org.ideoholic.timetable.engine.feasibility;

public interface FeasibilityValidator {

    String name();

    FeasibilityCheckResult validate(FeasibilityContext context);
}
