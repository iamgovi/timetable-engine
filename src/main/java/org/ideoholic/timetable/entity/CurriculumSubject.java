package org.ideoholic.timetable.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;

@Entity
@Table(name = "curriculum_subject",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_curriculum_subject",
                        columnNames = {"curriculum_id", "subject_id"})
        })
@Data
public class CurriculumSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "curriculum_id",
            nullable = false)
    private Curriculum curriculum;

    @ManyToOne
    @JoinColumn(name = "subject_id",
            nullable = false)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private SubjectCategory category;

    @Column(name = "weekly_periods",
            nullable = false)
    private Integer weeklyPeriods;

    @Column(name = "daily_period_limit")
    private Integer dailyPeriodLimit;

    @Column(name = "requirement_type")
    private String requirementType;

    @Column(name = "stream_name")
    private String streamName;

    @Column(name = "elective_group")
    private String electiveGroup;

    @Column(name = "is_optional")
    private Boolean optionalSubject = false;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "is_active")
    private Boolean active = true;
}
