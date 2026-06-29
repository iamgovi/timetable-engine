package org.ideoholic.timetable.engine.scoring.rules;

import org.ideoholic.timetable.engine.scoring.ScoreCalculator;
import org.ideoholic.timetable.engine.scoring.ScoringContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HeavySubjectBalanceScore
        extends ScoringRuleSupport
        implements ScoreCalculator {

    private final int heavyClusterPenalty;

    private final int balancedPairBonus;

    public HeavySubjectBalanceScore(
            @Value("${timetable.scoring.heavy-subject-weight:150}")
            int heavyClusterPenalty,
            @Value("${timetable.scoring.heavy-light-balance-bonus:30}")
            int balancedPairBonus) {

        this.heavyClusterPenalty = heavyClusterPenalty;
        this.balancedPairBonus = balancedPairBonus;
    }

    @Override
    public String name() {
        return "HeavySubjectBalanceScore";
    }

    @Override
    public int calculate(
            ScoringContext context) {

        boolean currentHeavy = isHeavy(context.getCategoryName());
        boolean previousHeavy = isHeavy(previousPeriodCategory(context));

        if (currentHeavy && previousHeavy) {
            return -heavyClusterPenalty;
        }

        if (currentHeavy != previousHeavy) {
            return balancedPairBonus;
        }

        return 0;
    }
}
