package org.ideoholic.timetable.engine.strategy;

import java.util.Map;

import org.ideoholic.timetable.entity.Subject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DailyDistributionTracker {

    private final int dailyRepetitionPenalty;

    private final int consecutiveSubjectPenalty;

    public DailyDistributionTracker(
            @Value("${timetable.strategy.daily-repetition-penalty:1000}")
            int dailyRepetitionPenalty,
            @Value("${timetable.strategy.consecutive-subject-penalty:500}")
            int consecutiveSubjectPenalty) {

        this.dailyRepetitionPenalty = dailyRepetitionPenalty;
        this.consecutiveSubjectPenalty = consecutiveSubjectPenalty;
    }

    public boolean respectsDailyLimit(
            Subject subject,
            Map<Long, Integer> daySubjectCount) {

        if (subject == null || subject.getId() == null || subject.getDailyPeriods() == null) {
            return true;
        }

        return daySubjectCount.getOrDefault(subject.getId(), 0) < subject.getDailyPeriods();
    }

    public int penalty(
            Subject subject,
            Map<Long, Integer> daySubjectCount,
            Long previousPeriodSubjectId) {

        if (subject == null || subject.getId() == null) {
            return 0;
        }

        int penalty = daySubjectCount.getOrDefault(subject.getId(), 0) * dailyRepetitionPenalty;

        if (subject.getId().equals(previousPeriodSubjectId)) {
            penalty += consecutiveSubjectPenalty;
        }

        return penalty;
    }
}
