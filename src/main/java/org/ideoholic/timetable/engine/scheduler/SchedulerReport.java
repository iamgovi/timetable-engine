package org.ideoholic.timetable.engine.scheduler;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SchedulerReport {

    private final Set<Long> processedClassIds = new LinkedHashSet<>();

    private int classesProcessed;

    private int sectionsProcessed;

    private int assignmentsGenerated;

    private int failedSections;

    private long executionDurationMillis;

    private List<String> failedSectionLabels = new ArrayList<>();

    private List<String> warnings = new ArrayList<>();

    public void recordSuccess(
            SchedulingTask task,
            int assignmentCount) {

        recordSection(task);
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

    public int getClassesProcessed() {
        return classesProcessed;
    }

    public void setClassesProcessed(
            int classesProcessed) {

        this.classesProcessed = classesProcessed;
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
