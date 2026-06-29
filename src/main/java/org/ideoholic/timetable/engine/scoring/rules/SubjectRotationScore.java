package org.ideoholic.timetable.engine.scoring.rules;

import org.ideoholic.timetable.engine.scoring.ScoreCalculator;
import org.ideoholic.timetable.engine.scoring.ScoringContext;
import org.ideoholic.timetable.entity.Subject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SubjectRotationScore
        extends ScoringRuleSupport
        implements ScoreCalculator {

    private final int consecutivePenalty;

    private final int previousDaySamePeriodPenalty;

    private final int rotationBonus;

    public SubjectRotationScore(
            @Value("${timetable.scoring.consecutive-subject-penalty:500}")
            int consecutivePenalty,
            @Value("${timetable.scoring.previous-day-same-period-penalty:100}")
            int previousDaySamePeriodPenalty,
            @Value("${timetable.scoring.rotation-bonus:25}")
            int rotationBonus) {

        this.consecutivePenalty = consecutivePenalty;
        this.previousDaySamePeriodPenalty = previousDaySamePeriodPenalty;
        this.rotationBonus = rotationBonus;
    }

    @Override
    public String name() {
        return "SubjectRotationScore";
    }

    @Override
    public int calculate(
            ScoringContext context) {

        if (context.getGenerationContext() == null) {
            return 0;
        }

        Subject subject = context.getSubject();
        int score = 0;

        if (isSameSubject(
                context.getGenerationContext().getPreviousPeriodSubjectId(),
                subject)) {
            score -= consecutivePenalty;
        } else {
            score += rotationBonus;
        }

        if (isSameSubject(
                context.getGenerationContext().getPreviousDayPeriodSubjectId(),
                subject)) {
            score -= previousDaySamePeriodPenalty;
        }

        return score;
    }
}
