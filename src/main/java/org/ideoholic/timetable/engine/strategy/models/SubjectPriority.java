package org.ideoholic.timetable.engine.strategy.models;

import org.ideoholic.timetable.entity.Subject;

public class SubjectPriority {

    private final Subject subject;

    private final int priority;

    private final RemainingDemand remainingDemand;

    private final String categoryName;

    public SubjectPriority(
            Subject subject,
            int priority,
            RemainingDemand remainingDemand,
            String categoryName) {

        this.subject = subject;
        this.priority = priority;
        this.remainingDemand = remainingDemand;
        this.categoryName = categoryName;
    }

    public Subject getSubject() {
        return subject;
    }

    public int getPriority() {
        return priority;
    }

    public RemainingDemand getRemainingDemand() {
        return remainingDemand;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
