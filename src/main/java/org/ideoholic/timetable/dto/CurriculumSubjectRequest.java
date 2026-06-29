package org.ideoholic.timetable.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CurriculumSubjectRequest {

    @NotNull
    private Long subjectId;

    private Long categoryId;

    @NotNull
    @Min(0)
    private Integer weeklyPeriods;

    @Min(0)
    private Integer dailyPeriodLimit;

    private String requirementType;

    private String streamName;

    private String electiveGroup;

    private Boolean optionalSubject;

    private Integer displayOrder;

    private Boolean active;
}
