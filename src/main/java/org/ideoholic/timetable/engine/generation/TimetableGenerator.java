package org.ideoholic.timetable.engine.generation;

import java.util.List;

import org.ideoholic.timetable.entity.TimetableAssignment;

public interface TimetableGenerator {

    List<TimetableAssignment> generate(
            TimetableGenerationPlan plan);
}
