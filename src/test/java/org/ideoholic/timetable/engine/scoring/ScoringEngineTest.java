package org.ideoholic.timetable.engine.scoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.ideoholic.timetable.engine.planning.models.TeacherUtilization;
import org.ideoholic.timetable.engine.scoring.rules.ActivityPlacementScore;
import org.ideoholic.timetable.engine.scoring.rules.DailyDistributionScore;
import org.ideoholic.timetable.engine.scoring.rules.RemainingDemandScore;
import org.ideoholic.timetable.engine.scoring.rules.TeacherWorkloadScore;
import org.ideoholic.timetable.engine.scoring.rules.WeeklyDistributionScore;
import org.ideoholic.timetable.engine.strategy.models.GenerationContext;
import org.ideoholic.timetable.engine.strategy.models.RemainingDemand;
import org.ideoholic.timetable.engine.strategy.models.SubjectPriority;
import org.ideoholic.timetable.entity.Period;
import org.ideoholic.timetable.entity.Subject;
import org.ideoholic.timetable.entity.Teacher;
import org.junit.jupiter.api.Test;

class ScoringEngineTest {

    @Test
    void aggregatesIndividualRuleScores() {
        ScoringEngine engine = new ScoringEngine(Arrays.asList(
                new RemainingDemandScore(100),
                new DailyDistributionScore(1000)));

        ScoringResult result = engine.score(context(
                subject(1L, "Mathematics"),
                teacher(1L),
                "Core",
                4));

        assertEquals(2, result.getRuleScores().size());
        assertEquals(400, result.getRuleScores().get("RemainingDemandScore"));
        assertEquals(400, result.getFinalScore());
    }

    @Test
    void ranksCandidateAllocationsByFinalScore() {
        ScoringEngine engine = new ScoringEngine(Arrays.asList(
                new RemainingDemandScore(100),
                new TeacherWorkloadScore(80, 20)));

        ScoringContext highDemand = context(
                subject(1L, "Mathematics"),
                teacher(1L),
                "Core",
                5);
        ScoringContext lowerDemand = context(
                subject(2L, "Physical Education"),
                teacher(2L),
                "Activity",
                1);

        List<ScoringResult> results = engine.rank(Arrays.asList(lowerDemand, highDemand));

        assertEquals(1L, results.get(0).getSubject().getId());
    }

    @Test
    void configurationWeightsChangeScores() {
        ScoringContext context = context(
                subject(1L, "Science"),
                teacher(1L),
                "Theory",
                3);

        int lowWeightScore = new RemainingDemandScore(10).calculate(context);
        int highWeightScore = new RemainingDemandScore(100).calculate(context);

        assertTrue(highWeightScore > lowWeightScore);
    }

    @Test
    void weeklyDistributionPenalizesRepeatedPeriodPlacement() {
        ScoringContext context = context(
                subject(1L, "Mathematics"),
                teacher(1L),
                "Core",
                3);
        context.getGenerationContext().getSamePeriodSubjectCount().put(1L, 2);

        int score = new WeeklyDistributionScore(125).calculate(context);

        assertEquals(-250, score);
    }

    @Test
    void teacherWorkloadUsesPlanningUtilization() {
        ScoringContext context = context(
                subject(1L, "Mathematics"),
                teacher(1L),
                "Core",
                3);

        TeacherUtilization utilization = new TeacherUtilization();
        utilization.setTeacherId(1L);
        utilization.setUtilizationPercent(75.0);
        context.getTeacherUtilizationByTeacherId().put(1L, utilization);

        int score = new TeacherWorkloadScore(80, 20).calculate(context);

        assertEquals(20, score);
    }

    @Test
    void activityPlacementRewardsLatePeriods() {
        ScoringContext early = context(
                subject(1L, "Physical Education"),
                teacher(1L),
                "Activity",
                1);
        ScoringContext late = context(
                subject(1L, "Physical Education"),
                teacher(1L),
                "Activity",
                1);
        late.getGenerationContext().getPeriod().setPeriodNumber(7);

        ActivityPlacementScore rule = new ActivityPlacementScore(60, 40);

        assertTrue(rule.calculate(late) > rule.calculate(early));
    }

    private ScoringContext context(
            Subject subject,
            Teacher teacher,
            String categoryName,
            int remainingPeriods) {

        Period period = new Period();
        period.setId(1L);
        period.setPeriodNumber(1);

        GenerationContext generationContext = new GenerationContext();
        generationContext.setPeriod(period);
        generationContext.setPeriodsInDay(7);
        generationContext.setWeeklySubjectCount(new HashMap<>());
        generationContext.setDaySubjectCount(new HashMap<>());
        generationContext.setSamePeriodSubjectCount(new HashMap<>());

        ScoringContext context = new ScoringContext();
        context.setGenerationContext(generationContext);
        context.setSubjectPriority(new SubjectPriority(
                subject,
                0,
                new RemainingDemand(remainingPeriods, 0),
                categoryName));
        context.setTeacher(teacher);
        context.setTeacherUsageCount(new HashMap<>());
        context.setTeacherUtilizationByTeacherId(new HashMap<>());
        context.setCategoryBySubjectId(new HashMap<>());
        context.getCategoryBySubjectId().put(subject.getId(), categoryName);
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

    private Teacher teacher(
            Long id) {

        Teacher teacher = new Teacher();
        teacher.setId(id);
        teacher.setTeacherName("Teacher " + id);
        return teacher;
    }
}
