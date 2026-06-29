package org.ideoholic.timetable.engine.scheduler;

import java.util.List;
import java.util.stream.Collectors;

import org.ideoholic.timetable.dto.SimpleTimetableGenerationRequest;
import org.ideoholic.timetable.entity.TimetableAssignment;
import org.ideoholic.timetable.service.TimetableGenerationService;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SectionScheduler {

    private final TimetableGenerationService generationService;

    public Object schedule(
            SchedulingTask task) {

        SimpleTimetableGenerationRequest request = new SimpleTimetableGenerationRequest();
        request.setSectionId(task.getSection().getId());
        request.setWorkingDayIds(task.getWorkingDays()
                .stream()
                .map(workingDay -> workingDay.getId())
                .collect(Collectors.toList()));
        return generationService.generateSingleSectionTimetable(request);
    }

    public int assignmentCount(
            Object result) {

        if (!(result instanceof List<?>)) {
            return 0;
        }

        int count = 0;
        for (Object item : (List<?>) result) {
            if (item instanceof TimetableAssignment) {
                count++;
            }
        }
        return count;
    }
}
