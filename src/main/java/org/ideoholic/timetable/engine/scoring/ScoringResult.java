package org.ideoholic.timetable.engine.scoring;

import java.util.LinkedHashMap;
import java.util.Map;

import org.ideoholic.timetable.engine.strategy.models.SubjectPriority;
import org.ideoholic.timetable.entity.Subject;
import org.ideoholic.timetable.entity.Teacher;

public class ScoringResult {

    private final SubjectPriority subjectPriority;

    private final Teacher teacher;

    private final Map<String, Integer> ruleScores = new LinkedHashMap<>();

    private int finalScore;

    public ScoringResult(
            SubjectPriority subjectPriority,
            Teacher teacher) {

        this.subjectPriority = subjectPriority;
        this.teacher = teacher;
    }

    public SubjectPriority getSubjectPriority() {
        return subjectPriority;
    }

    public Subject getSubject() {
        return subjectPriority == null ? null : subjectPriority.getSubject();
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public Map<String, Integer> getRuleScores() {
        return ruleScores;
    }

    public int getFinalScore() {
        return finalScore;
    }

    public void addRuleScore(
            String ruleName,
            int score) {

        ruleScores.put(ruleName, score);
        finalScore += score;
    }
}
