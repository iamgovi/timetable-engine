package org.ideoholic.timetable.engine.generation;

import org.ideoholic.timetable.entity.Subject;

class SubjectCandidate {

    private final Subject subject;

    private final int priority;

    SubjectCandidate(
            Subject subject,
            int priority) {

        this.subject = subject;
        this.priority = priority;
    }

    Subject getSubject() {
        return subject;
    }

    int getPriority() {
        return priority;
    }
}
