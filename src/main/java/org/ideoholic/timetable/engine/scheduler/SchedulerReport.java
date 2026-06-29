package org.ideoholic.timetable.engine.scheduler;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SchedulerReport {

    private final Set<Long> processedClassIds = new LinkedHashSet<>();

    private String sessionId;

    private GenerationStatus status;

    private int classesProcessed;

    private int completedSections;

    private int sectionsProcessed;

    private int assignmentsGenerated;

    private int failedSections;

    private long executionDurationMillis;

    private List<String> failedSectionLabels = new ArrayList<>();

    private List<String> warnings = new ArrayList<>();

    public static SchedulerReport feasibilityFailed(
            GenerationSession session,
            List<String> warnings) {

        SchedulerReport report = new SchedulerReport();
        report.applySession(session);
        report.setStatus(GenerationStatus.FAILED);
        report.setWarnings(warnings == null ? new ArrayList<>() : warnings);
        return report;
    }

    public void recordSuccess(
            SchedulingTask task,
            int assignmentCount) {

        recordSection(task);
        completedSections++;
        assignmentsGenerated += Math.max(0, assignmentCount);
    }

    public void recordFailure(
            SchedulingTask task,
            String warning) {

        recordSection(task);
        failedSections++;
        failedSectionLabels.add(sectionLabel(task));
        if (warning != null && !warning.isBlank()) {
            warnings.add(warning);
        }
    }

    public void applySession(
            GenerationSession session) {

        if (session == null) {
            return;
        }

        sessionId = session.getSessionId();
        status = session.getStatus();
        completedSections = session.getCompletedSections();
        failedSections = session.getFailedSections();
        assignmentsGenerated = session.getAssignmentsGenerated();
        executionDurationMillis = session.getExecutionDurationMillis();
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(
            String sessionId) {

        this.sessionId = sessionId;
    }

    public GenerationStatus getStatus() {
        return status;
    }

    public void setStatus(
            GenerationStatus status) {

        this.status = status;
    }

    public int getClassesProcessed() {
        return classesProcessed;
    }

    public void setClassesProcessed(
            int classesProcessed) {

        this.classesProcessed = classesProcessed;
    }

    public int getCompletedSections() {
        return completedSections;
    }

    public void setCompletedSections(
            int completedSections) {

        this.completedSections = completedSections;
    }

    public int getSectionsProcessed() {
        return sectionsProcessed;
    }

    public void setSectionsProcessed(
            int sectionsProcessed) {

        this.sectionsProcessed = sectionsProcessed;
    }

    public int getAssignmentsGenerated() {
        return assignmentsGenerated;
    }

    public void setAssignmentsGenerated(
            int assignmentsGenerated) {

        this.assignmentsGenerated = assignmentsGenerated;
    }

    public int getFailedSections() {
        return failedSections;
    }

    public void setFailedSections(
            int failedSections) {

        this.failedSections = failedSections;
    }

    public long getExecutionDurationMillis() {
        return executionDurationMillis;
    }

    public void setExecutionDurationMillis(
            long executionDurationMillis) {

        this.executionDurationMillis = executionDurationMillis;
    }

    public List<String> getFailedSectionLabels() {
        return failedSectionLabels;
    }

    public void setFailedSectionLabels(
            List<String> failedSectionLabels) {

        this.failedSectionLabels = failedSectionLabels;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(
            List<String> warnings) {

        this.warnings = warnings;
    }

    private void recordSection(
            SchedulingTask task) {

        sectionsProcessed++;
        if (task != null
                && task.getClassMaster() != null
                && task.getClassMaster().getId() != null) {
            processedClassIds.add(task.getClassMaster().getId());
            classesProcessed = processedClassIds.size();
        }
    }

    private String sectionLabel(
            SchedulingTask task) {

        if (task == null || task.getSection() == null) {
            return "unknown section";
        }

        String className = task.getClassMaster() == null
                ? "unknown class"
                : task.getClassMaster().getClassName();
        return className + " - " + task.getSection().getSectionName();
    }
}
