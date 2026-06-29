package org.ideoholic.timetable.dto;

import java.util.List;

public class SchoolTimetableGenerationRequest {

    private Long academicYearId;

    private List<Long> classIds;

    private List<Long> workingDayIds;

    public Long getAcademicYearId() {
        return academicYearId;
    }

    public void setAcademicYearId(
            Long academicYearId) {

        this.academicYearId = academicYearId;
    }

    public List<Long> getClassIds() {
        return classIds;
    }

    public void setClassIds(
            List<Long> classIds) {

        this.classIds = classIds;
    }

    public List<Long> getWorkingDayIds() {
        return workingDayIds;
    }

    public void setWorkingDayIds(
            List<Long> workingDayIds) {

        this.workingDayIds = workingDayIds;
    }
}
