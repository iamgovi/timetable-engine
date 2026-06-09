package org.ideoholic.timetable.entity;

import javax.persistence.*;

import lombok.Data;

@Entity
@Table(name = "teacher")
@Data
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "teacher_name",
            nullable = false)
    private String teacherName;

    @Column(name = "employee_code",
            nullable = false,
            unique = true)
    private String employeeCode;

    @Column(unique = true)
    private String email;

    private String mobile;

    @Column(name = "is_active")
    private Boolean active;
}