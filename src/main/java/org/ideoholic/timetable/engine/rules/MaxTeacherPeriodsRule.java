package org.ideoholic.timetable.engine.rules;

import org.ideoholic.timetable.engine.context.TimetableContext;
import org.ideoholic.timetable.repository.TimetableAssignmentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@Order(2)
@RequiredArgsConstructor
public class MaxTeacherPeriodsRule
        implements TimetableRule {

    private final TimetableAssignmentRepository repository;

    @Value("${timetable.max-teacher-periods-per-day:6}")
    private int maxPeriodsPerDay;

    @Override
    public boolean validate(
            TimetableContext context) {

        if (context.getTeacher() == null
                || context.getWorkingDay() == null) {
            return false;
        }

        long assignedPeriods = repository.countByTeacherAndWorkingDay(
                context.getTeacher(),
                context.getWorkingDay());

        return assignedPeriods < maxPeriodsPerDay;
    }

    @Override
    public String getRuleName() {
        return "Max Teacher Periods Rule";
    }
}