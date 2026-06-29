package org.ideoholic.timetable.engine.strategy;

import java.util.Map;

import org.ideoholic.timetable.entity.Subject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WeeklyDistributionTracker {

    private final int samePeriodPenalty;

    private final int previousDaySamePeriodPenalty;

    public WeeklyDistributionTracker(
            @Value("${timetable.strategy.same-period-weekly-penalty:125}")
            int samePeriodPenalty,
            @Value("${timetable.strategy.previous-day-same-period-penalty:100}")
            int previousDaySamePeriodPenalty) {

        this.samePeriodPenalty = samePeriodPenalty;
        this.previousDaySamePeriodPenalty = previousDaySamePeriodPenalty;
    }

    public int penalty(
            Subject subject,
            Map<Long, Integer> samePeriodSubjectCount,
            Long previousDayPeriodSubjectId) {

        if (subject == null || subject.getId() == null) {
            return 0;
        }

        int penalty = samePeriodSubjectCount.getOrDefault(subject.getId(), 0)
                * samePeriodPenalty;

        if (subject.getId().equals(previousDayPeriodSubjectId)) {
            penalty += previousDaySamePeriodPenalty;
        }

        return penalty;
    }
}
