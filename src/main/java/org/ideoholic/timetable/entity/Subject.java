package org.ideoholic.timetable.entity;

import javax.persistence.*;

import lombok.Data;

@Entity
@Table(name = "subject")
@Data
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject_name",
            nullable = false,
            unique = true)
    private String subjectName;

    @Column(name = "subject_code",
            nullable = false,
            unique = true)
    private String subjectCode;

    @Column(name = "is_lab")
    private Boolean lab;

    @Column(name = "weekly_periods")
    private Integer weeklyPeriods;

    @ManyToOne
    @JoinColumn(name = "prerequisite_subject_id")
    private Subject prerequisiteSubject;

    @Column(name = "is_active")
    private Boolean active;
}