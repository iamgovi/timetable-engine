package org.ideoholic.timetable.engine.rules;

import org.ideoholic.timetable.engine.context.TimetableContext;

public interface TimetableRule {

    boolean validate(
            TimetableContext context);

    String getRuleName();
}