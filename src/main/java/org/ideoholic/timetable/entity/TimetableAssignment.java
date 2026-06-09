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
}