package org.ideoholic.timetable.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RuleTestResponse {

    private Boolean valid;

    private String message;
}