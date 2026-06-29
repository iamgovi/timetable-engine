package org.ideoholic.timetable.engine.rules;

import org.ideoholic.timetable.engine.context.TimetableContext;
import org.ideoholic.timetable.entity.Subject;
import org.ideoholic.timetable.entity.TimetableAssignment;
import org.ideoholic.timetable.repository.TimetableAssignmentRepository;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@Order(6)
@RequiredArgsConstructor
public class ConsecutiveSubjectRule implements TimetableRule {

    private final TimetableAssignmentRepository repository;

    @Override
    public boolean validate(TimetableContext context) {
        if (context.getSection() == null
                || context.getWorkingDay() == null
                || context.getPeriod() == null
                || context.getSubject() == null) {
            return false;
        }

        Subject subject = context.getSubject();

        if (Boolean.TRUE.equals(subject.getLab())) {
            return true;
        }

        Integer previousPeriodNumber = context.getPeriod().getPeriodNumber() - 1;
        if (previousPeriodNumber == null || previousPeriodNumber < 1) {
            return true;
        }

        TimetableAssignment previousAssignment = repository
                .findBySectionAndWorkingDayAndPeriodPeriodNumber(
                        context.getSection(),
                        context.getWorkingDay(),
                        previousPeriodNumber);

        if (previousAssignment == null
                || previousAssignment.getSubject() == null) {
            return true;
        }

        return !subject.getId().equals(previousAssignment.getSubject().getId());
    }

    @Override
    public String getRuleName() {
        return "Consecutive Subject Rule";
    }
}
