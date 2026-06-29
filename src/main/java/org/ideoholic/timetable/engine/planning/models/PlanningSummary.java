package org.ideoholic.timetable.engine.planning.models;

public class PlanningSummary {

    private int totalClasses;

    private int totalSections;

    private int totalCurriculumPeriods;

    private int totalTeachers;

    private int subjectsWithShortages;

    private int teachersOverCapacity;

    private int teachersUnderUtilized;

    private boolean generationFeasible;

    public int getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(int totalClasses) {
        this.totalClasses = totalClasses;
    }

    public int getTotalSections() {
        return totalSections;
    }

    public void setTotalSections(int totalSections) {
        this.totalSections = totalSections;
    }

    public int getTotalCurriculumPeriods() {
        return totalCurriculumPeriods;
    }

    public void setTotalCurriculumPeriods(int totalCurriculumPeriods) {
        this.totalCurriculumPeriods = totalCurriculumPeriods;
    }

    public int getTotalTeachers() {
        return totalTeachers;
    }

    public void setTotalTeachers(int totalTeachers) {
        this.totalTeachers = totalTeachers;
    }

    public int getSubjectsWithShortages() {
        return subjectsWithShortages;
    }

    public void setSubjectsWithShortages(int subjectsWithShortages) {
        this.subjectsWithShortages = subjectsWithShortages;
    }

    public int getTeachersOverCapacity() {
        return teachersOverCapacity;
    }

    public void setTeachersOverCapacity(int teachersOverCapacity) {
        this.teachersOverCapacity = teachersOverCapacity;
    }

    public int getTeachersUnderUtilized() {
        return teachersUnderUtilized;
    }

    public void setTeachersUnderUtilized(int teachersUnderUtilized) {
        this.teachersUnderUtilized = teachersUnderUtilized;
    }

    public boolean isGenerationFeasible() {
        return generationFeasible;
    }

    public void setGenerationFeasible(boolean generationFeasible) {
        this.generationFeasible = generationFeasible;
    }
}
