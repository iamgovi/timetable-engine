package org.ideoholic.timetable.engine.scoring.rules;

import java.util.Locale;

import org.ideoholic.timetable.engine.scoring.ScoringContext;
import org.ideoholic.timetable.entity.Subject;

abstract class ScoringRuleSupport {

    boolean isSameSubject(
            Long subjectId,
            Subject subject) {

        return subjectId != null
                && subject != null
                && subject.getId() != null
                && subjectId.equals(subject.getId());
    }

    boolean isHeavy(
            String categoryName) {

        String normalized = normalize(categoryName);
        return "core".equals(normalized) || "theory".equals(normalized);
    }

    boolean isActivityOrLight(
            String categoryName) {

        String normalized = normalize(categoryName);
        return "activity".equals(normalized)
                || "lab".equals(normalized)
                || "practical".equals(normalized)
                || "elective".equals(normalized);
    }

    String previousPeriodCategory(
            ScoringContext context) {

        if (context.getGenerationContext() == null
                || context.getGenerationContext().getPreviousPeriodSubjectId() == null) {
            return null;
        }

        return context.getCategoryBySubjectId()
                .get(context.getGenerationContext().getPreviousPeriodSubjectId());
    }

    double dayPosition(
            ScoringContext context) {

        if (context.getGenerationContext() == null
                || context.getGenerationContext().getPeriod() == null
                || context.getGenerationContext().getPeriod().getPeriodNumber() == null
                || context.getGenerationContext().getPeriodsInDay() <= 1) {
            return 0.5;
        }

        return Math.max(0.0, Math.min(1.0,
                (context.getGenerationContext().getPeriod().getPeriodNumber() - 1.0)
                        / (context.getGenerationContext().getPeriodsInDay() - 1.0)));
    }

    private String normalize(
            String value) {

        return value == null ? "" : value.toLowerCase(Locale.ROOT).trim();
    }
}
