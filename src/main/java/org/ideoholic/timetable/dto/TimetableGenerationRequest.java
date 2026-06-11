package org.ideoholic.timetable.dto;

import java.util.List;

import lombok.Data;

@Data
public class TimetableGenerationRequest {

    private List<Long> teacherIds;

    private List<Long> sectionIds;

    private List<Long> classIds;

    private String workingDayName;

    public List<Long> getTeacherIds() {
        return teacherIds;
    }

    public void setTeacherIds(List<Long> teacherIds) {
        this.teacherIds = teacherIds;
    }

    public List<Long> getSectionIds() {
        return sectionIds;
    }

    public void setSectionIds(List<Long> sectionIds) {
        this.sectionIds = sectionIds;
    }

    public List<Long> getClassIds() {
        return classIds;
    }

    public void setClassIds(List<Long> classIds) {
        this.classIds = classIds;
    }

    public String getWorkingDayName() {
        return workingDayName;
    }

    public void setWorkingDayName(String workingDayName) {
        this.workingDayName = workingDayName;
    }
}
