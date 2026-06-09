package org.ideoholic.timetable.engine.rules;

import org.ideoholic.timetable.engine.context.TimetableContext;
import org.ideoholic.timetable.entity.TeacherAvailability;
import org.ideoholic.timetable.repository.TeacherAvailabilityRepository;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TeacherAvailabilityRule
        implements TimetableRule {

    private final TeacherAvailabilityRepository repository;

    @Override
    public boolean validate(
            TimetableContext context) {

        TeacherAvailability availability =
                repository.findByTeacherAndWorkingDayAndPeriod(
                        context.getTeacher(),
                        context.getWorkingDay(),
                        context.getPeriod());

        if (availability == null) {
            return false;
        }

        return Boolean.TRUE.equals(
                availability.getAvailable());
    }

    @Override
    public String getRuleName() {

        return "Teacher Availability Rule";
    }
}