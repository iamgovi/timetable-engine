package org.ideoholic.timetable.engine.planning.models;

public class TeacherCapacity {

    private Long teacherId;

    private String teacherName;

    private int maxWeeklyPeriods;

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

    public int getMaxWeeklyPeriods() {
        return maxWeeklyPeriods;
    }

    public void setMaxWeeklyPeriods(int maxWeeklyPeriods) {
        this.maxWeeklyPeriods = maxWeeklyPeriods;
    }
}
