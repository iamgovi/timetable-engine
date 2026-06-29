package org.ideoholic.timetable.engine.scoring;

public interface ScoreCalculator {

    String name();

    int calculate(ScoringContext context);
}
