package org.ideoholic.timetable.engine.scoring;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class ScoringEngine {

    private final List<ScoreCalculator> calculators;

    public ScoringEngine(
            List<ScoreCalculator> calculators) {

        this.calculators = calculators;
    }

    public ScoringResult score(
            ScoringContext context) {

        ScoringResult result = new ScoringResult(
                context.getSubjectPriority(),
                context.getTeacher());

        for (ScoreCalculator calculator : calculators) {
            result.addRuleScore(calculator.name(), calculator.calculate(context));
        }

        return result;
    }

    public List<ScoringResult> rank(
            List<ScoringContext> contexts) {

        return contexts.stream()
                .map(this::score)
                .sorted(Comparator
                        .comparingInt(ScoringResult::getFinalScore)
                        .reversed()
                        .thenComparing(result -> result.getSubject().getId())
                        .thenComparing(result -> result.getTeacher().getId()))
                .collect(Collectors.toList());
    }
}
