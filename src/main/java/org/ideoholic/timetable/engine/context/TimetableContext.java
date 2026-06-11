package org.ideoholic.timetable.engine.context;

import org.ideoholic.timetable.entity.Period;
import org.ideoholic.timetable.entity.Section;
import org.ideoholic.timetable.entity.Subject;
import org.ideoholic.timetable.entity.Teacher;
import org.ideoholic.timetable.entity.WorkingDay;

import lombok.Data;

@Data
public class TimetableContext {

    private Teacher teacher;

    private Subject subject;

    private Section section;

    private WorkingDay workingDay;

    private Period period;

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public WorkingDay getWorkingDay() {
        return workingDay;
    }

    public void setWorkingDay(WorkingDay workingDay) {
        this.workingDay = workingDay;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }
}