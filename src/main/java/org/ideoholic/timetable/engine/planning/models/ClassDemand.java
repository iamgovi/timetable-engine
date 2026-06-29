package org.ideoholic.timetable.engine.planning.models;

import java.util.ArrayList;
import java.util.List;

public class ClassDemand {

    private Long classId;

    private String className;

    private Long curriculumId;

    private int sectionCount;

    private int weeklyPeriodsPerSection;

    private int totalWeeklyPeriods;

    private List<SubjectDemand> subjects = new ArrayList<>();

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Long getCurriculumId() {
        return curriculumId;
    }

    public void setCurriculumId(Long curriculumId) {
        this.curriculumId = curriculumId;
    }

    public int getSectionCount() {
        return sectionCount;
    }

    public void setSectionCount(int sectionCount) {
        this.sectionCount = sectionCount;
    }

    public int getWeeklyPeriodsPerSection() {
        return weeklyPeriodsPerSection;
    }

    public void setWeeklyPeriodsPerSection(int weeklyPeriodsPerSection) {
        this.weeklyPeriodsPerSection = weeklyPeriodsPerSection;
    }

    public int getTotalWeeklyPeriods() {
        return totalWeeklyPeriods;
    }

    public void setTotalWeeklyPeriods(int totalWeeklyPeriods) {
        this.totalWeeklyPeriods = totalWeeklyPeriods;
    }

    public List<SubjectDemand> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<SubjectDemand> subjects) {
        this.subjects = subjects;
    }
}
