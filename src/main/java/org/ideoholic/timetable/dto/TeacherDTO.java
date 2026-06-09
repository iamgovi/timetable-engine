package org.ideoholic.timetable.dto;

import lombok.Data;

@Data
public class TeacherDTO {

    private Long id;

    private String teacherName;

    private String employeeCode;

    private String email;

    private String mobile;

    private Boolean active;
}