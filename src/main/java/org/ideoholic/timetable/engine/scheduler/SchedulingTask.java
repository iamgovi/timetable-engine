package org.ideoholic.timetable.engine.scheduler;

import java.util.ArrayList;
import java.util.List;

import org.ideoholic.timetable.entity.AcademicYear;
import org.ideoholic.timetable.entity.ClassMaster;
import org.ideoholic.timetable.entity.Section;
import org.ideoholic.timetable.entity.WorkingDay;

/**
 * A single section-level scheduling unit.
 */
public class SchedulingTask {

    private AcademicYear academicYear;

    private ClassMaster classMaster;

    private Section section;

    private List<WorkingDay> workingDays = new ArrayList<>();

    private int priorityScore;

    public SchedulingTask() {
    }

    public SchedulingTask(
            AcademicYear academicYear,
            ClassMaster classMaster,
            Section section,
            List<WorkingDay> workingDays) {

        this.academicYear = academicYear;
        this.classMaster = classMaster;
        this.section = section;
        this.workingDays = workingDays;
    }

    public AcademicYear getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(
            AcademicYear academicYear) {

        this.academicYear = academicYear;
    }

    public ClassMaster getClassMaster() {
        return classMaster;
    }

    public void setClassMaster(
            ClassMaster classMaster) {

        this.classMaster = classMaster;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(
            Section section) {

        this.section = section;
    }

    public List<WorkingDay> getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(
            List<WorkingDay> workingDays) {

        this.workingDays = workingDays;
    }

    public int getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(
            int priorityScore) {

        this.priorityScore = priorityScore;
    }
}
