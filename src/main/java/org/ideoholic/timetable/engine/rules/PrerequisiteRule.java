package org.ideoholic.timetable.engine.rules;

import org.ideoholic.timetable.engine.context.TimetableContext;
import org.ideoholic.timetable.entity.Subject;
import org.ideoholic.timetable.repository.TimetableAssignmentRepository;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@Order(4)
@RequiredArgsConstructor
public class PrerequisiteRule
        implements TimetableRule {

    private final TimetableAssignmentRepository repository;

    @Override
    public boolean validate(
            TimetableContext context) {

        Subject subject = context.getSubject();

        if (subject == null) {
            return true;
        }

        Subject prerequisite = subject.getPrerequisiteSubject();

        if (prerequisite == null) {
            return true;
        }

        if (context.getSection() == null
                || context.getWorkingDay() == null
                || context.getPeriod() == null) {
            return false;
        }

        return repository.existsBySectionAndWorkingDayAndSubjectAndPeriodPeriodNumberLessThan(
                context.getSection(),
                context.getWorkingDay(),
                prerequisite,
                context.getPeriod().getPeriodNumber());
    }

    @Override
    public String getRuleName() {
        return "Prerequisite Rule";
    }
}