package org.ideoholic.timetable.engine.strategy;

import java.util.List;

import org.ideoholic.timetable.engine.strategy.models.GenerationContext;
import org.ideoholic.timetable.engine.strategy.models.SubjectPriority;

/**
 * Orders candidate subjects for timetable generation.
 *
 * The rule engine remains responsible for deciding whether a selected subject
 * and teacher can actually be allocated.
 */
public interface GenerationStrategy {

    List<SubjectPriority> prioritize(GenerationContext context);
}
