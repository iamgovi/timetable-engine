package org.ideoholic.timetable.entity;

import javax.persistence.*;

import lombok.Data;

@Entity
@Table(name = "academic_year")
@Data
public class AcademicYear {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "year_name",
            nullable = false,
            unique = true)
    private String yearName;

    @Column(name = "is_current")
    private Boolean current;
}