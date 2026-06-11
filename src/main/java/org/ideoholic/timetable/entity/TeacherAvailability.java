package org.ideoholic.timetable.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "teacher_availability")
@Data
public class TeacherAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "working_day_id")
    private WorkingDay workingDay;

    @ManyToOne
    @JoinColumn(name = "period_id")
    private Period period;

    @Column(name = "is_available")
    private Boolean available;

    public Teacher getTeacher() {
        return teacher;
    }

    public WorkingDay getWorkingDay() {
        return workingDay;
    }

    public Period getPeriod() {
        return period;
    }

    public Boolean getAvailable() {
        return available;
    }
}
