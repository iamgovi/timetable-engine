package org.ideoholic.timetable.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simplified timetable generation request for a single class and selected working days.
 * 
 * This is used for Phase 1: Single Class + Multiple Sections + Monday generation.
 * 
 * Example:
 * {
 *   "classId": 1,
 *   "workingDayIds": [1, 2, 3, 4, 5]
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleTimetableGenerationRequest {

    /**
     * The ID of the class to generate timetables for (e.g., Grade 8).
     */
    private Long classId;

    /**
     * Legacy section identifier. If provided without classId, the request will still generate for that single section.
     */
    private Long sectionId;

    /**
     * The IDs of the working days to generate sequentially.
     */
    private List<Long> workingDayIds;
}
