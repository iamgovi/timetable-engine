package org.ideoholic.timetable.engine.strategy;

import java.util.Map;

import org.ideoholic.timetable.engine.strategy.models.RemainingDemand;
import org.ideoholic.timetable.entity.Subject;
import org.springframework.stereotype.Component;

@Component
public class RemainingDemandTracker {

    public RemainingDemand calculate(
            Subject subject,
            Integer curriculumWeeklyPeriods,
            Map<Long, Integer> weeklySubjectCount) {

        int requiredPeriods = curriculumWeeklyPeriods == null
                ? subjectWeeklyPeriods(subject)
                : curriculumWeeklyPeriods;

        int allocatedPeriods = subject == null || subject.getId() == null
                ? 0
                : weeklySubjectCount.getOrDefault(subject.getId(), 0);

        return new RemainingDemand(requiredPeriods, allocatedPeriods);
    }

    private int subjectWeeklyPeriods(
            Subject subject) {

        if (subject == null || subject.getWeeklyPeriods() == null) {
            return 0;
        }

        return subject.getWeeklyPeriods();
    }
}
