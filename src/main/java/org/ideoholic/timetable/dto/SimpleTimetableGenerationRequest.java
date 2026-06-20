package org.ideoholic.timetable.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simplified timetable generation request for a single class and single working day.
 * 
 * This is used for Phase 1: Single Class + Multiple Sections + Monday generation.
 * 
 * Example:
 * {
 *   "classId": 1,
 *   "workingDayId": 1
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
     * The ID of the working day (e.g., Monday)
     */
    private Long workingDayId;
}
