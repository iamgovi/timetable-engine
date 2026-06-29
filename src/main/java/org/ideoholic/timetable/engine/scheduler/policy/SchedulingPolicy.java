package org.ideoholic.timetable.engine.scheduler.policy;

import java.util.List;

import org.ideoholic.timetable.engine.planning.models.AcademicPlan;
import org.ideoholic.timetable.engine.scheduler.SchedulingTask;

public interface SchedulingPolicy {

    String name();

    List<SchedulingTask> prioritize(
            List<SchedulingTask> tasks,
            AcademicPlan academicPlan);
}
