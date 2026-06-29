package org.ideoholic.timetable.engine.strategy;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;

import org.ideoholic.timetable.engine.strategy.models.GenerationContext;
import org.ideoholic.timetable.engine.strategy.models.RemainingDemand;
import org.ideoholic.timetable.entity.Period;
import org.ideoholic.timetable.entity.Subject;
import org.junit.jupiter.api.Test;

class SubjectPriorityCalculatorTest {

    private final DailyDistributionTracker dailyDistributionTracker =
            new DailyDistributionTracker(1000, 500);

    private final WeeklyDistributionTracker weeklyDistributionTracker =
            new WeeklyDistributionTracker(125, 100);

    private final SubjectPriorityCalculator calculator =
            new SubjectPriorityCalculator(100, 80, 60, 20);

    @Test
    void dailyRepetitionPenaltyPushesRepeatedSubjectDown() {
        Subject mathematics = subject(1L, "Mathematics");
        Subject science = subject(2L, "Science");
        GenerationContext context = context(2);
        context.getDaySubjectCount().put(mathematics.getId(), 1);

        int repeatedPriority = calculator.calculate(
                mathematics,
                "Core",
                new RemainingDemand(6, 1),
                context,
                dailyDistributionTracker,
                weeklyDistributionTracker);
        int freshPriority = calculator.calculate(
                science,
                "Theory",
                new RemainingDemand(6, 1),
                context,
                dailyDistributionTracker,
                weeklyDistributionTracker);

        assertTrue(freshPriority > repeatedPriority);
    }

    @Test
    void weeklySamePeriodPenaltyImprovesPlacementVariety() {
        Subject mathematics = subject(1L, "Mathematics");
        Subject science = subject(2L, "Science");
        GenerationContext context = context(1);
        context.getSamePeriodSubjectCount().put(mathematics.getId(), 3);

        int repeatedSlotPriority = calculator.calculate(
                mathematics,
                "Core",
                new RemainingDemand(6, 3),
                context,
                dailyDistributionTracker,
                weeklyDistributionTracker);
        int openSlotPriority = calculator.calculate(
                science,
                "Theory",
                new RemainingDemand(6, 3),
                context,
                dailyDistributionTracker,
                weeklyDistributionTracker);

        assertTrue(openSlotPriority > repeatedSlotPriority);
    }

    @Test
    void lightSubjectsReceiveLateDayPlacementPreference() {
        Subject pe = subject(1L, "Physical Education");
        GenerationContext early = context(1);
        GenerationContext late = context(7);

        int earlyPriority = calculator.calculate(
                pe,
                "Activity",
                new RemainingDemand(2, 0),
                early,
                dailyDistributionTracker,
                weeklyDistributionTracker);
        int latePriority = calculator.calculate(
                pe,
                "Activity",
                new RemainingDemand(2, 0),
                late,
                dailyDistributionTracker,
                weeklyDistributionTracker);

        assertTrue(latePriority > earlyPriority);
    }

    private GenerationContext context(
            int periodNumber) {

        Period period = new Period();
        period.setPeriodNumber(periodNumber);

        GenerationContext context = new GenerationContext();
        context.setPeriod(period);
        context.setPeriodsInDay(7);
        context.setDaySubjectCount(new HashMap<>());
        context.setSamePeriodSubjectCount(new HashMap<>());
        return context;
    }

    private Subject subject(
            Long id,
            String name) {

        Subject subject = new Subject();
        subject.setId(id);
        subject.setSubjectName(name);
        return subject;
    }
}
