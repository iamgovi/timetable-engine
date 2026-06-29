package org.ideoholic.timetable.engine.scheduler.policy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.ideoholic.timetable.engine.planning.models.AcademicPlan;
import org.ideoholic.timetable.engine.planning.models.ClassDemand;
import org.ideoholic.timetable.engine.planning.models.SubjectDemand;
import org.ideoholic.timetable.engine.planning.models.TeacherRequirement;
import org.ideoholic.timetable.engine.scheduler.SchedulingTask;
import org.springframework.stereotype.Component;

@Component
public class BalancedSchedulingPolicy implements SchedulingPolicy {

    @Override
    public String name() {
        return "balanced";
    }

    @Override
    public List<SchedulingTask> prioritize(
            List<SchedulingTask> tasks,
            AcademicPlan academicPlan) {

        Map<Long, Integer> sectionCountByClassId = sectionCountByClassId(tasks);
        List<SchedulingTask> prioritized = new ArrayList<>(tasks);
        for (SchedulingTask task : prioritized) {
            Long classId = classId(task);
            int curriculumDemandScore = curriculumDemand(academicPlan, classId) * 100;
            int sectionLoadScore = sectionCountByClassId.getOrDefault(classId, 0) * 10;
            int scarcityScore = teacherScarcity(academicPlan, classId) * 50;
            task.setPriorityScore(curriculumDemandScore + sectionLoadScore + scarcityScore);
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

    private int teacherScarcity(
            AcademicPlan academicPlan,
            Long classId) {

        if (academicPlan == null || classId == null) {
            return 0;
        }

        Set<Long> subjectIds = academicPlan.getSubjectDemands()
                .stream()
                .filter(demand -> classId.equals(demand.getClassId()))
                .map(SubjectDemand::getSubjectId)
                .collect(Collectors.toSet());

        return academicPlan.getTeacherRequirements()
                .stream()
                .filter(requirement -> subjectIds.contains(requirement.getSubjectId()))
                .mapToInt(TeacherRequirement::getAdditionalTeachersNeeded)
                .filter(shortage -> shortage > 0)
                .sum();
    }

    private Map<Long, Integer> sectionCountByClassId(
            List<SchedulingTask> tasks) {

        Map<Long, Integer> sectionCountByClassId = new LinkedHashMap<>();
        for (SchedulingTask task : tasks) {
            Long classId = classId(task);
            if (classId != null) {
                sectionCountByClassId.merge(classId, 1, Integer::sum);
            }
        }
        return sectionCountByClassId;
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
