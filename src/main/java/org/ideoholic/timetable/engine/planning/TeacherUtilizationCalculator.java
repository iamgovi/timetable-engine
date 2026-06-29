package org.ideoholic.timetable.engine.planning;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.ideoholic.timetable.engine.planning.models.SubjectDemand;
import org.ideoholic.timetable.engine.planning.models.TeacherCapacity;
import org.ideoholic.timetable.engine.planning.models.TeacherUtilization;
import org.ideoholic.timetable.entity.TeacherSubjectMapping;
import org.springframework.stereotype.Component;

@Component
public class TeacherUtilizationCalculator {

    public List<TeacherUtilization> calculate(
            List<SubjectDemand> subjectDemands,
            List<TeacherCapacity> teacherCapacities,
            List<TeacherSubjectMapping> mappings) {

        Map<Long, Integer> demandBySubjectId = subjectDemands.stream()
                .collect(Collectors.toMap(
                        SubjectDemand::getSubjectId,
                        SubjectDemand::getTotalWeeklyPeriods,
                        Integer::sum));

        Map<Long, List<TeacherSubjectMapping>> mappingsBySubjectId = mappings.stream()
                .filter(mapping -> mapping.getSubject() != null)
                .filter(mapping -> mapping.getTeacher() != null)
                .filter(mapping -> !Boolean.FALSE.equals(mapping.getTeacher().getActive()))
                .collect(Collectors.groupingBy(mapping -> mapping.getSubject().getId()));

        Map<Long, Integer> loadByTeacherId = new HashMap<>();
        Map<Long, Set<String>> subjectsByTeacherId = new HashMap<>();

        for (Map.Entry<Long, Integer> entry : demandBySubjectId.entrySet()) {
            List<TeacherSubjectMapping> subjectMappings = mappingsBySubjectId
                    .getOrDefault(entry.getKey(), new ArrayList<>());
            if (subjectMappings.isEmpty()) {
                continue;
            }

            int loadPerTeacher = (int) Math.ceil((double) entry.getValue() / subjectMappings.size());
            for (TeacherSubjectMapping mapping : subjectMappings) {
                Long teacherId = mapping.getTeacher().getId();
                loadByTeacherId.merge(teacherId, loadPerTeacher, Integer::sum);
                subjectsByTeacherId.computeIfAbsent(teacherId, id -> new HashSet<>())
                        .add(mapping.getSubject().getSubjectName());
            }
        }

        return teacherCapacities.stream()
                .sorted(Comparator.comparing(TeacherCapacity::getTeacherId))
                .map(capacity -> toUtilization(
                        capacity,
                        loadByTeacherId.getOrDefault(capacity.getTeacherId(), 0),
                        subjectsByTeacherId.getOrDefault(capacity.getTeacherId(), new HashSet<>())))
                .collect(Collectors.toList());
    }

    private TeacherUtilization toUtilization(
            TeacherCapacity capacity,
            int load,
            Set<String> subjects) {

        TeacherUtilization utilization = new TeacherUtilization();
        utilization.setTeacherId(capacity.getTeacherId());
        utilization.setTeacherName(capacity.getTeacherName());
        utilization.setSubjects(subjects.stream().sorted().collect(Collectors.toList()));
        utilization.setAssignedCurriculumLoad(load);
        utilization.setMaxWeeklyPeriods(capacity.getMaxWeeklyPeriods());

        double utilizationPercent = capacity.getMaxWeeklyPeriods() <= 0
                ? 0
                : (load * 100.0) / capacity.getMaxWeeklyPeriods();
        utilization.setUtilizationPercent(Math.round(utilizationPercent * 100.0) / 100.0);
        utilization.setOverCapacity(load > capacity.getMaxWeeklyPeriods());
        utilization.setUnderUtilized(load > 0 && utilizationPercent < 50.0);
        return utilization;
    }
}
