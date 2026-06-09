package org.ideoholic.timetable.dto;

import lombok.Data;

@Data
public class RuleTestRequest {

    private Long teacherId;

    private Long subjectId;

    private Long sectionId;

    private Long workingDayId;

    private Long periodId;
}