package org.ideoholic.timetable.engine.rules;

import org.ideoholic.timetable.engine.context.TimetableContext;
import org.ideoholic.timetable.repository.TimetableAssignmentRepository;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@Order(3)
@RequiredArgsConstructor
public class ConflictRule
        implements TimetableRule {

    private final TimetableAssignmentRepository repository;

    @Override
    public boolean validate(
            TimetableContext context) {

        if (context.getTeacher() == null
                || context.getWorkingDay() == null
                || context.getPeriod() == null) {
            return false;
        }

        return repository.findByTeacherAndWorkingDayAndPeriod(
                context.getTeacher(),
                context.getWorkingDay(),
                context.getPeriod()) == null;
    }

    @Override
    public String getRuleName() {
        return "Teacher Conflict Rule";
    }
}