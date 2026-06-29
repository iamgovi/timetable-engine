package org.ideoholic.timetable.engine.feasibility;

import java.util.ArrayList;
import java.util.List;

public class FeasibilityCheckResult {

    private final String checkName;

    private final List<ValidationIssue> errors = new ArrayList<>();

    private final List<ValidationIssue> warnings = new ArrayList<>();

    public FeasibilityCheckResult(
            String checkName) {

        this.checkName = checkName;
    }

    public String getCheckName() {
        return checkName;
    }

    public List<ValidationIssue> getErrors() {
        return errors;
    }

    public List<ValidationIssue> getWarnings() {
        return warnings;
    }

    public boolean passed() {
        return errors.isEmpty() && warnings.isEmpty();
    }

    public void error(
            String message,
            String recommendation) {

        errors.add(new ValidationIssue(checkName, "ERROR", message, recommendation));
    }

    public void warning(
            String message,
            String recommendation) {

        warnings.add(new ValidationIssue(checkName, "WARNING", message, recommendation));
    }
}
