package org.ideoholic.timetable.engine.strategy;

import java.util.Locale;

import org.ideoholic.timetable.engine.strategy.models.GenerationContext;
import org.ideoholic.timetable.engine.strategy.models.RemainingDemand;
import org.ideoholic.timetable.entity.Subject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SubjectPriorityCalculator {

    private final int remainingDemandWeight;

    private final int heavyEarlyBonus;

    private final int lightLateBonus;

    private final int neutralPlacementBonus;

    public SubjectPriorityCalculator(
            @Value("${timetable.strategy.remaining-demand-weight:100}")
            int remainingDemandWeight,
            @Value("${timetable.strategy.heavy-early-bonus:80}")
            int heavyEarlyBonus,
            @Value("${timetable.strategy.light-late-bonus:60}")
            int lightLateBonus,
            @Value("${timetable.strategy.neutral-placement-bonus:20}")
            int neutralPlacementBonus) {

        this.remainingDemandWeight = remainingDemandWeight;
        this.heavyEarlyBonus = heavyEarlyBonus;
        this.lightLateBonus = lightLateBonus;
        this.neutralPlacementBonus = neutralPlacementBonus;
    }

    public int calculate(
            Subject subject,
            String categoryName,
            RemainingDemand remainingDemand,
            GenerationContext context,
            DailyDistributionTracker dailyDistributionTracker,
            WeeklyDistributionTracker weeklyDistributionTracker) {

        int priority = remainingDemand.getRemainingPeriods() * remainingDemandWeight;
        priority -= dailyDistributionTracker.penalty(
                subject,
                context.getDaySubjectCount(),
                context.getPreviousPeriodSubjectId());
        priority -= weeklyDistributionTracker.penalty(
                subject,
                context.getSamePeriodSubjectCount(),
                context.getPreviousDayPeriodSubjectId());
        priority += placementBonus(categoryName, context);

        return priority;
    }

    private int placementBonus(
            String categoryName,
            GenerationContext context) {

        double dayPosition = dayPosition(context);
        if (isLight(categoryName)) {
            return (int) Math.round(dayPosition * lightLateBonus);
        }

        if (isHeavy(categoryName)) {
            return (int) Math.round((1.0 - dayPosition) * heavyEarlyBonus);
        }

        return neutralPlacementBonus;
    }

    private double dayPosition(
            GenerationContext context) {

        if (context.getPeriod() == null
                || context.getPeriod().getPeriodNumber() == null
                || context.getPeriodsInDay() <= 1) {
            return 0.5;
        }

        return Math.max(0.0, Math.min(1.0,
                (context.getPeriod().getPeriodNumber() - 1.0)
                        / (context.getPeriodsInDay() - 1.0)));
    }

    private boolean isHeavy(
            String categoryName) {

        String normalized = normalize(categoryName);
        return "core".equals(normalized) || "theory".equals(normalized);
    }

    private boolean isLight(
            String categoryName) {

        String normalized = normalize(categoryName);
        return "lab".equals(normalized)
                || "practical".equals(normalized)
                || "activity".equals(normalized)
                || "elective".equals(normalized);
    }

    private String normalize(
            String value) {

        return value == null ? "" : value.toLowerCase(Locale.ROOT).trim();
    }
}
