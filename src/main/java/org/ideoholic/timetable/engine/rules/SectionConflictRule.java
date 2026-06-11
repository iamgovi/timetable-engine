package org.ideoholic.timetable.engine.rules;

import org.ideoholic.timetable.engine.context.TimetableContext;
import org.ideoholic.timetable.repository.TimetableAssignmentRepository;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@Order(1)
@RequiredArgsConstructor
public class SectionConflictRule implements TimetableRule {

    private final TimetableAssignmentRepository repository;

    @Override
    public boolean validate(
            TimetableContext context) {

        if (context.getSection() == null
                || context.getWorkingDay() == null
                || context.getPeriod() == null) {
            return false;
        }

        return !repository.existsBySectionAndWorkingDayAndPeriod(
                context.getSection(),
                context.getWorkingDay(),
                context.getPeriod());
    }

    @Override
    public String getRuleName() {
        return "Section Conflict Rule";
    }
}
