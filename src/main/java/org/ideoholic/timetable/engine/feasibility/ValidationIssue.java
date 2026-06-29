package org.ideoholic.timetable.engine.feasibility;

public class ValidationIssue {

    private String checkName;

    private String severity;

    private String message;

    private String recommendation;

    public ValidationIssue() {
    }

    public ValidationIssue(
            String checkName,
            String severity,
            String message,
            String recommendation) {

        this.checkName = checkName;
        this.severity = severity;
        this.message = message;
        this.recommendation = recommendation;
    }

    public String getCheckName() {
        return checkName;
    }

    public void setCheckName(String checkName) {
        this.checkName = checkName;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
}
