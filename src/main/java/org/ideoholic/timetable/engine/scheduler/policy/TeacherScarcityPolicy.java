package org.ideoholic.timetable.engine.scheduler.policy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.ideoholic.timetable.engine.planning.models.AcademicPlan;
import org.ideoholic.timetable.engine.planning.models.SubjectDemand;
import org.ideoholic.timetable.engine.planning.models.TeacherRequirement;
import org.ideoholic.timetable.engine.planning.models.TeacherUtilization;
import org.ideoholic.timetable.engine.scheduler.SchedulingTask;
import org.springframework.stereotype.Component;

@Component
public class TeacherScarcityPolicy implements SchedulingPolicy {

    @Override
    public String name() {
        return "teacher-scarcity";
    }

    @Override
    public List<SchedulingTask> prioritize(
            List<SchedulingTask> tasks,
            AcademicPlan academicPlan) {

        List<SchedulingTask> prioritized = new ArrayList<>(tasks);
        for (SchedulingTask task : prioritized) {
            Long classId = classId(task);
            int shortageScore = shortage(academicPlan, classId) * 100;
            int utilizationScore = (int) Math.round(maxUtilization(academicPlan, classId));
            task.setPriorityScore(shortageScore + utilizationScore);
        }
        prioritized.sort(taskComparator());
        return prioritized;
    }

    private int shortage(
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

    private double maxUtilization(
            AcademicPlan academicPlan,
            Long classId) {

        if (academicPlan == null || classId == null) {
            return 0.0;
        }

        Set<String> subjectNames = academicPlan.getSubjectDemands()
                .stream()
                .filter(demand -> classId.equals(demand.getClassId()))
                .map(SubjectDemand::getSubjectName)
                .collect(Collectors.toSet());

        return academicPlan.getTeacherUtilizations()
                .stream()
                .filter(utilization -> utilization.getSubjects()
                        .stream()
                        .anyMatch(subjectNames::contains))
                .mapToDouble(TeacherUtilization::getUtilizationPercent)
                .max()
                .orElse(0.0);
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
