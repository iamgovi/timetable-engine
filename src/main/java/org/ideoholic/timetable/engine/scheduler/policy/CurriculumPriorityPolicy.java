package org.ideoholic.timetable.engine.scheduler.policy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.ideoholic.timetable.engine.planning.models.AcademicPlan;
import org.ideoholic.timetable.engine.planning.models.ClassDemand;
import org.ideoholic.timetable.engine.scheduler.SchedulingTask;
import org.springframework.stereotype.Component;

@Component
public class CurriculumPriorityPolicy implements SchedulingPolicy {

    @Override
    public String name() {
        return "curriculum";
    }

    @Override
    public List<SchedulingTask> prioritize(
            List<SchedulingTask> tasks,
            AcademicPlan academicPlan) {

        List<SchedulingTask> prioritized = new ArrayList<>(tasks);
        for (SchedulingTask task : prioritized) {
            task.setPriorityScore(curriculumDemand(academicPlan, classId(task)) * 100);
        }
        prioritized.sort(taskComparator());
        return prioritized;
    }

    private int curriculumDemand(
            AcademicPlan academicPlan,
            Long classId) {

        if (academicPlan == null || classId == null) {
            return 0;
        }
        return academicPlan.getClassDemands()
                .stream()
                .filter(demand -> classId.equals(demand.getClassId()))
                .mapToInt(ClassDemand::getWeeklyPeriodsPerSection)
                .findFirst()
                .orElse(0);
    }

    private Long classId(
            SchedulingTask task) {

        return task == null || task.getClassMaster() == null
                ? null
                : task.getClassMaster().getId();
    }

    private Comparator<SchedulingTask> taskComparator() {
        return Comparator
                .comparingInt(SchedulingTask::getPriorityScore)
                .reversed()
                .thenComparing(task -> classId(task) == null ? Long.MAX_VALUE : classId(task))
                .thenComparing(task -> task.getSection() == null
                        || task.getSection().getSectionName() == null
                                ? ""
                                : task.getSection().getSectionName())
                .thenComparing(task -> task.getSection() == null
                        || task.getSection().getId() == null
                                ? Long.MAX_VALUE
                                : task.getSection().getId());
    }
}
