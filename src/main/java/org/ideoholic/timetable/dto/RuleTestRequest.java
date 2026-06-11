package org.ideoholic.timetable.dto;

import lombok.Data;

@Data
public class RuleTestRequest {

    private Long teacherId;

    private Long subjectId;

    private Long sectionId;

    private Long workingDayId;

    private Long periodId;

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public Long getWorkingDayId() {
        return workingDayId;
    }

    public void setWorkingDayId(Long workingDayId) {
        this.workingDayId = workingDayId;
    }

    public Long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(Long periodId) {
        this.periodId = periodId;
    }
}