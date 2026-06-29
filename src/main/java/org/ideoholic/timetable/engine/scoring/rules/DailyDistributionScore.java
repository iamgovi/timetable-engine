package org.ideoholic.timetable.engine.scoring.rules;

import org.ideoholic.timetable.engine.scoring.ScoreCalculator;
import org.ideoholic.timetable.engine.scoring.ScoringContext;
import org.ideoholic.timetable.entity.Subject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DailyDistributionScore
        implements ScoreCalculator {

    private final int weight;

    public DailyDistributionScore(
            @Value("${timetable.scoring.daily-distribution-weight:1000}")
            int weight) {

        this.weight = weight;
    }

    @Override
    public String name() {
        return "DailyDistributionScore";
    }

    @Override
    public int calculate(
            ScoringContext context) {

        Subject subject = context.getSubject();
        if (subject == null || subject.getId() == null
                || context.getGenerationContext() == null) {
            return 0;
        }

        int allocationsToday = context.getGenerationContext()
                .getDaySubjectCount()
                .getOrDefault(subject.getId(), 0);

        return -allocationsToday * weight;
    }
}
