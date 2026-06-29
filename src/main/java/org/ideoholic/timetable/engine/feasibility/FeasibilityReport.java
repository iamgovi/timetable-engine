package org.ideoholic.timetable.engine.feasibility;

import java.util.ArrayList;
import java.util.List;

public class FeasibilityReport {

    private boolean feasible = true;

    private ValidationSummary summary = new ValidationSummary();

    private List<ValidationIssue> errors = new ArrayList<>();

    private List<ValidationIssue> warnings = new ArrayList<>();

    private List<String> recommendations = new ArrayList<>();

    public boolean isFeasible() {
        return feasible;
    }

    public void setFeasible(boolean feasible) {
        this.feasible = feasible;
    }

    public ValidationSummary getSummary() {
        return summary;
    }

    public void setSummary(ValidationSummary summary) {
        this.summary = summary;
    }

    public List<ValidationIssue> getErrors() {
        return errors;
    }

    public void setErrors(List<ValidationIssue> errors) {
        this.errors = errors;
    }

    public List<ValidationIssue> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<ValidationIssue> warnings) {
        this.warnings = warnings;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }
}
