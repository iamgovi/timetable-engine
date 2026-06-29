package org.ideoholic.timetable.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CurriculumRequest {

    @NotBlank
    private String curriculumName;

    @NotNull
    private Long classId;

    @NotNull
    private Long academicYearId;

    private String description;

    private Boolean active;
}
