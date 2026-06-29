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
@Table(name = "curriculum",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_curriculum_class_year",
                        columnNames = {"class_id", "academic_year_id"})
        })
@Data
public class Curriculum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "curriculum_name",
            nullable = false)
    private String curriculumName;

    @ManyToOne
    @JoinColumn(name = "class_id",
            nullable = false)
    private ClassMaster classMaster;

    @ManyToOne
    @JoinColumn(name = "academic_year_id",
            nullable = false)
    private AcademicYear academicYear;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active")
    private Boolean active = true;
}
