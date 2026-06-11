package org.ideoholic.timetable.entity;

import javax.persistence.*;

import lombok.Data;

@Entity
@Table(name = "section")
@Data
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "section_name",
            nullable = false)
    private String sectionName;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassMaster classMaster;

    @ManyToOne
    @JoinColumn(name = "academic_year_id")
    private AcademicYear academicYear;

    public Long getId() {
        return id;
    }
}
