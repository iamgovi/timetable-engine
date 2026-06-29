package org.ideoholic.timetable.engine.scheduler;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.ideoholic.timetable.engine.planning.models.AcademicPlan;
import org.ideoholic.timetable.engine.planning.models.ClassDemand;
import org.ideoholic.timetable.engine.planning.models.SubjectDemand;
import org.ideoholic.timetable.engine.planning.models.TeacherRequirement;
import org.springframework.stereotype.Component;

@Component
public class SchedulingPriorityCalculator {

    public int calculate(
            SchedulingTask task,
            AcademicPlan academicPlan,
            Map<Long, Integer> sectionCountByClassId) {

        if (task == null || task.getClassMaster() == null || task.getClassMaster().getId() == null) {
            return 0;
        }

        Long classId = task.getClassMaster().getId();
        ClassDemand classDemand = classDemand(academicPlan, classId);
        int curriculumDemandScore = classDemand == null
                ? 0
                : classDemand.getWeeklyPeriodsPerSection() * 100;
        int sectionLoadScore = sectionCountByClassId.getOrDefault(classId, 0) * 10;
        int scarcityScore = teacherScarcityScore(academicPlan, classId);

        return curriculumDemandScore + sectionLoadScore + scarcityScore;
    }

    private ClassDemand classDemand(
            AcademicPlan academicPlan,
            Long classId) {

        if (academicPlan == null) {
            return null;
        }

        return academicPlan.getClassDemands()
                .stream()
                .filter(demand -> classId.equals(demand.getClassId()))
                .findFirst()
                .orElse(null);
    }

    private int teacherScarcityScore(
            AcademicPlan academicPlan,
            Long classId) {

        if (academicPlan == null) {
            return 0;
        }

        Set<Long> classSubjectIds = academicPlan.getSubjectDemands()
                .stream()
                .filter(demand -> classId.equals(demand.getClassId()))
                .map(SubjectDemand::getSubjectId)
                .collect(Collectors.toSet());

        return academicPlan.getTeacherRequirements()
                .stream()
                .filter(requirement -> classSubjectIds.contains(requirement.getSubjectId()))
                .mapToInt(TeacherRequirement::getAdditionalTeachersNeeded)
                .filter(shortage -> shortage > 0)
                .sum() * 50;
    }
}
