package org.ideoholic.timetable.engine.planning.models;

import java.util.ArrayList;
import java.util.List;

public class TeacherUtilization {

    private Long teacherId;

    private String teacherName;

    private List<String> subjects = new ArrayList<>();

    private int assignedCurriculumLoad;

    private int maxWeeklyPeriods;

    private double utilizationPercent;

    private boolean overCapacity;

    private boolean underUtilized;

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public int getAssignedCurriculumLoad() {
        return assignedCurriculumLoad;
    }

    public void setAssignedCurriculumLoad(int assignedCurriculumLoad) {
        this.assignedCurriculumLoad = assignedCurriculumLoad;
    }

    public int getMaxWeeklyPeriods() {
        return maxWeeklyPeriods;
    }

    public void setMaxWeeklyPeriods(int maxWeeklyPeriods) {
        this.maxWeeklyPeriods = maxWeeklyPeriods;
    }

    public double getUtilizationPercent() {
        return utilizationPercent;
    }

    public void setUtilizationPercent(double utilizationPercent) {
        this.utilizationPercent = utilizationPercent;
    }

    public boolean isOverCapacity() {
        return overCapacity;
    }

    public void setOverCapacity(boolean overCapacity) {
        this.overCapacity = overCapacity;
    }

    public boolean isUnderUtilized() {
        return underUtilized;
    }

    public void setUnderUtilized(boolean underUtilized) {
        this.underUtilized = underUtilized;
    }
}
