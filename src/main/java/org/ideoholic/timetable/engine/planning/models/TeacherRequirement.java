package org.ideoholic.timetable.engine.planning.models;

public class TeacherRequirement {

    private Long subjectId;

    private String subjectName;

    private int requiredPeriods;

    private int assignedTeachers;

    private int availableCapacity;

    private int requiredTeachers;

    private int additionalTeachersNeeded;

    private String status;

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getRequiredPeriods() {
        return requiredPeriods;
    }

    public void setRequiredPeriods(int requiredPeriods) {
        this.requiredPeriods = requiredPeriods;
    }

    public int getAssignedTeachers() {
        return assignedTeachers;
    }

    public void setAssignedTeachers(int assignedTeachers) {
        this.assignedTeachers = assignedTeachers;
    }

    public int getAvailableCapacity() {
        return availableCapacity;
    }

    public void setAvailableCapacity(int availableCapacity) {
        this.availableCapacity = availableCapacity;
    }

    public int getRequiredTeachers() {
        return requiredTeachers;
    }

    public void setRequiredTeachers(int requiredTeachers) {
        this.requiredTeachers = requiredTeachers;
    }

    public int getAdditionalTeachersNeeded() {
        return additionalTeachersNeeded;
    }

    public void setAdditionalTeachersNeeded(int additionalTeachersNeeded) {
        this.additionalTeachersNeeded = additionalTeachersNeeded;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
