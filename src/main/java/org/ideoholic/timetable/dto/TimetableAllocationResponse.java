package org.ideoholic.timetable.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TimetableAllocationResponse {

    private Boolean success;

    private String message;
}
