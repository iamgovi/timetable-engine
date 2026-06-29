package org.ideoholic.timetable.engine.planning.models;

public class SubjectDemand {

    private Long classId;

    private String className;

    private Long subjectId;

    private String subjectName;

    private String categoryName;

    private String requirementType;

    private Boolean optionalSubject;

    private int weeklyPeriodsPerSection;

    private int sectionCount;

    private int totalWeeklyPeriods;

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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getRequirementType() {
        return requirementType;
    }

    public void setRequirementType(String requirementType) {
        this.requirementType = requirementType;
    }

    public Boolean getOptionalSubject() {
        return optionalSubject;
    }

    public void setOptionalSubject(Boolean optionalSubject) {
        this.optionalSubject = optionalSubject;
    }

    public int getWeeklyPeriodsPerSection() {
        return weeklyPeriodsPerSection;
    }

    public void setWeeklyPeriodsPerSection(int weeklyPeriodsPerSection) {
        this.weeklyPeriodsPerSection = weeklyPeriodsPerSection;
    }

    public int getSectionCount() {
        return sectionCount;
    }

    public void setSectionCount(int sectionCount) {
        this.sectionCount = sectionCount;
    }

    public int getTotalWeeklyPeriods() {
        return totalWeeklyPeriods;
    }

    public void setTotalWeeklyPeriods(int totalWeeklyPeriods) {
        this.totalWeeklyPeriods = totalWeeklyPeriods;
    }
}
