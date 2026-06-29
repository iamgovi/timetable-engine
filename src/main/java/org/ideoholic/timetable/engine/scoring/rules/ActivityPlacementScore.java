package org.ideoholic.timetable.engine.scoring.rules;

import org.ideoholic.timetable.engine.scoring.ScoreCalculator;
import org.ideoholic.timetable.engine.scoring.ScoringContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ActivityPlacementScore
        extends ScoringRuleSupport
        implements ScoreCalculator {

    private final int activityPlacementWeight;

    private final int heavyMorningWeight;

    public ActivityPlacementScore(
            @Value("${timetable.scoring.activity-placement-weight:60}")
            int activityPlacementWeight,
            @Value("${timetable.scoring.heavy-morning-weight:40}")
            int heavyMorningWeight) {

        this.activityPlacementWeight = activityPlacementWeight;
        this.heavyMorningWeight = heavyMorningWeight;
    }

    @Override
    public String name() {
        return "ActivityPlacementScore";
    }

    @Override
    public int calculate(
            ScoringContext context) {

        double dayPosition = dayPosition(context);
        if (isActivityOrLight(context.getCategoryName())) {
            return (int) Math.round(dayPosition * activityPlacementWeight);
        }

        if (isHeavy(context.getCategoryName())) {
            return (int) Math.round((1.0 - dayPosition) * heavyMorningWeight);
        }

        return 0;
    }
}
