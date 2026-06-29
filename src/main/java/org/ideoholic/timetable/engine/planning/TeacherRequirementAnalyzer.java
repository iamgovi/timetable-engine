package org.ideoholic.timetable.engine.planning;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ideoholic.timetable.engine.planning.models.SubjectDemand;
import org.ideoholic.timetable.engine.planning.models.TeacherCapacity;
import org.ideoholic.timetable.engine.planning.models.TeacherRequirement;
import org.ideoholic.timetable.entity.TeacherSubjectMapping;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TeacherRequirementAnalyzer {

    private final int maxWeeklyPeriods;

    public TeacherRequirementAnalyzer(
            @Value("${timetable.teacher-max-weekly-periods:30}") int maxWeeklyPeriods) {

        this.maxWeeklyPeriods = maxWeeklyPeriods;
    }

    public List<TeacherRequirement> analyze(
            List<SubjectDemand> subjectDemands,
            List<TeacherCapacity> teacherCapacities,
            List<TeacherSubjectMapping> mappings) {

        Map<Long, Integer> capacityByTeacherId = teacherCapacities.stream()
                .collect(Collectors.toMap(
                        TeacherCapacity::getTeacherId,
                        TeacherCapacity::getMaxWeeklyPeriods));

        Map<Long, List<TeacherSubjectMapping>> mappingsBySubjectId = mappings.stream()
                .filter(mapping -> mapping.getSubject() != null)
                .filter(mapping -> mapping.getTeacher() != null)
                .filter(mapping -> !Boolean.FALSE.equals(mapping.getTeacher().getActive()))
                .collect(Collectors.groupingBy(mapping -> mapping.getSubject().getId()));

        List<TeacherRequirement> requirements = new ArrayList<>();
        subjectDemands.stream()
                .sorted(Comparator.comparing(SubjectDemand::getSubjectName))
                .forEach(demand -> {
                    List<TeacherSubjectMapping> subjectMappings = mappingsBySubjectId
                            .getOrDefault(demand.getSubjectId(), new ArrayList<>());
                    int availableCapacity = subjectMappings.stream()
                            .mapToInt(mapping -> capacityByTeacherId.getOrDefault(
                                    mapping.getTeacher().getId(),
                                    0))
                            .sum();
                    int assignedTeachers = subjectMappings.size();
                    int requiredTeachers = requiredTeachers(
                            demand.getTotalWeeklyPeriods(),
                            maxWeeklyPeriods);
                    int additionalTeachersNeeded = Math.max(0, requiredTeachers - assignedTeachers);

                    TeacherRequirement requirement = new TeacherRequirement();
                    requirement.setSubjectId(demand.getSubjectId());
                    requirement.setSubjectName(demand.getSubjectName());
                    requirement.setRequiredPeriods(demand.getTotalWeeklyPeriods());
                    requirement.setAssignedTeachers(assignedTeachers);
                    requirement.setAvailableCapacity(availableCapacity);
                    requirement.setRequiredTeachers(requiredTeachers);
                    requirement.setAdditionalTeachersNeeded(additionalTeachersNeeded);
                    requirement.setStatus(availableCapacity >= demand.getTotalWeeklyPeriods()
                            ? "OK"
                            : "SHORTAGE");
                    requirements.add(requirement);
                });

        return requirements;
    }

    private int requiredTeachers(
            int requiredPeriods,
            int maxWeeklyPeriods) {

        if (requiredPeriods <= 0 || maxWeeklyPeriods <= 0) {
            return 0;
        }

        return (int) Math.ceil((double) requiredPeriods / maxWeeklyPeriods);
    }
}
