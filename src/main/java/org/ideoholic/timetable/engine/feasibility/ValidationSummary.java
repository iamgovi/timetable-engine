package org.ideoholic.timetable.engine.feasibility;

public class ValidationSummary {

    private int totalChecks;

    private int passedChecks;

    private int failedChecks;

    private int warningChecks;

    private int errors;

    private int warnings;

    public int getTotalChecks() {
        return totalChecks;
    }

    public void setTotalChecks(int totalChecks) {
        this.totalChecks = totalChecks;
    }

    public int getPassedChecks() {
        return passedChecks;
    }

    public void setPassedChecks(int passedChecks) {
        this.passedChecks = passedChecks;
    }

    public int getFailedChecks() {
        return failedChecks;
    }

    public void setFailedChecks(int failedChecks) {
        this.failedChecks = failedChecks;
    }

    public int getWarningChecks() {
        return warningChecks;
    }

    public void setWarningChecks(int warningChecks) {
        this.warningChecks = warningChecks;
    }

    public int getErrors() {
        return errors;
    }

    public void setErrors(int errors) {
        this.errors = errors;
    }

    public int getWarnings() {
        return warnings;
    }

    public void setWarnings(int warnings) {
        this.warnings = warnings;
    }
}
