package org.ideoholic.timetable.engine.scoring.rules;

import org.ideoholic.timetable.engine.scoring.ScoreCalculator;
import org.ideoholic.timetable.engine.scoring.ScoringContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RemainingDemandScore
        implements ScoreCalculator {

    private final int weight;

    public RemainingDemandScore(
            @Value("${timetable.scoring.remaining-demand-weight:100}")
            int weight) {

        this.weight = weight;
    }

    @Override
    public String name() {
        return "RemainingDemandScore";
    }

    @Override
    public int calculate(
            ScoringContext context) {

        if (context.getSubjectPriority() == null
                || context.getSubjectPriority().getRemainingDemand() == null) {
            return 0;
        }

        return context.getSubjectPriority()
                .getRemainingDemand()
                .getRemainingPeriods() * weight;
    }
}
