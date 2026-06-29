package org.ideoholic.timetable.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class SubjectCategoryRequest {

    @NotBlank
    private String categoryName;

    private String description;

    private Boolean active;
}
