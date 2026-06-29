package org.ideoholic.timetable.engine.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.ideoholic.timetable.engine.strategy.models.RemainingDemand;
import org.ideoholic.timetable.entity.Subject;
import org.junit.jupiter.api.Test;

class RemainingDemandTrackerTest {

    private final RemainingDemandTracker tracker = new RemainingDemandTracker();

    @Test
    void calculatesRemainingDemandFromCurriculumRequirement() {
        Subject subject = subject(1L, "Mathematics", 3, 2);
        Map<Long, Integer> weeklyCount = new HashMap<>();
        weeklyCount.put(1L, 2);

        RemainingDemand demand = tracker.calculate(subject, 6, weeklyCount);

        assertEquals(6, demand.getRequiredPeriods());
        assertEquals(2, demand.getAllocatedPeriods());
        assertEquals(4, demand.getRemainingPeriods());
    }

    @Test
    void fallsBackToSubjectWeeklyPeriodsWhenCurriculumIsUnavailable() {
        Subject subject = subject(1L, "Science", 5, 2);

        RemainingDemand demand = tracker.calculate(subject, null, new HashMap<>());

        assertEquals(5, demand.getRequiredPeriods());
        assertEquals(5, demand.getRemainingPeriods());
    }

    private Subject subject(
            Long id,
            String name,
            int weeklyPeriods,
            int dailyPeriods) {

        Subject subject = new Subject();
        subject.setId(id);
        subject.setSubjectName(name);
        subject.setWeeklyPeriods(weeklyPeriods);
        subject.setDailyPeriods(dailyPeriods);
        return subject;
    }
}
