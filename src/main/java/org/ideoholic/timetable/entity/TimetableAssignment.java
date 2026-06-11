package org.ideoholic.timetable.entity;

import javax.persistence.*;

import lombok.Data;

@Entity
@Table(name = "timetable_assignment")
@Data
public class TimetableAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;

    @ManyToOne
    @JoinColumn(name = "working_day_id")
    private WorkingDay workingDay;

    @ManyToOne
    @JoinColumn(name = "period_id")
    private Period period;

    public Teacher getTeacher() {
        return teacher;
    }

    public Subject getSubject() {
        return subject;
    }

    public Section getSection() {
        return section;
    }

    public WorkingDay getWorkingDay() {
        return workingDay;
    }

    public Period getPeriod() {
        return period;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public void setWorkingDay(WorkingDay workingDay) {
        this.workingDay = workingDay;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }
}
