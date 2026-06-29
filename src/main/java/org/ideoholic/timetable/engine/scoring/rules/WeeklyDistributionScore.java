package org.ideoholic.timetable.engine.scoring.rules;

import org.ideoholic.timetable.engine.scoring.ScoreCalculator;
import org.ideoholic.timetable.engine.scoring.ScoringContext;
import org.ideoholic.timetable.entity.Subject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WeeklyDistributionScore
        extends ScoringRuleSupport
        implements ScoreCalculator {

    private final int weight;

    public WeeklyDistributionScore(
            @Value("${timetable.scoring.weekly-distribution-weight:125}")
            int weight) {

        this.weight = weight;
    }

    @Override
    public String name() {
        return "WeeklyDistributionScore";
    }

    @Override
    public int calculate(
            ScoringContext context) {

        Subject subject = context.getSubject();
        if (subject == null || subject.getId() == null
                || context.getGenerationContext() == null) {
            return 0;
        }

        int samePeriodCount = context.getGenerationContext()
                .getSamePeriodSubjectCount()
                .getOrDefault(subject.getId(), 0);

        return -samePeriodCount * weight;
    }
}
