package org.ideoholic.timetable.engine.rules;

import org.ideoholic.timetable.engine.context.TimetableContext;
import org.ideoholic.timetable.entity.Subject;
import org.ideoholic.timetable.repository.TimetableAssignmentRepository;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@Order(5)
@RequiredArgsConstructor
public class DailySubjectLimitRule implements TimetableRule {

    private final TimetableAssignmentRepository repository;

    @Override
    public boolean validate(TimetableContext context) {
        if (context.getSection() == null
                || context.getWorkingDay() == null
                || context.getSubject() == null) {
            return false;
        }

        Subject subject = context.getSubject();
        Integer dailyLimit = subject.getDailyPeriods();

        if (dailyLimit == null) {
            return true;
        }

        long assignedCount = repository.countBySectionAndSubjectAndWorkingDay(
                context.getSection(),
                subject,
                context.getWorkingDay());

        return assignedCount < dailyLimit;
    }

    @Override
    public String getRuleName() {
        return "Daily Subject Limit Rule";
    }
}
