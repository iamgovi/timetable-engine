package org.ideoholic.timetable.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;

@Entity
@Table(name = "subject_category",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_subject_category_name", columnNames = "category_name")
        })
@Data
public class SubjectCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_name",
            nullable = false)
    private String categoryName;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active")
    private Boolean active = true;
}
