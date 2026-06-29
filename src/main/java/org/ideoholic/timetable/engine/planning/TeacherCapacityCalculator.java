package org.ideoholic.timetable.engine.planning;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.ideoholic.timetable.engine.planning.models.TeacherCapacity;
import org.ideoholic.timetable.entity.Teacher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TeacherCapacityCalculator {

    private final int maxWeeklyPeriods;

    public TeacherCapacityCalculator(
            @Value("${timetable.teacher-max-weekly-periods:30}") int maxWeeklyPeriods) {

        this.maxWeeklyPeriods = maxWeeklyPeriods;
    }

    public List<TeacherCapacity> calculate(
            List<Teacher> teachers) {

        List<TeacherCapacity> capacities = new ArrayList<>();
        teachers.stream()
                .filter(teacher -> !Boolean.FALSE.equals(teacher.getActive()))
                .sorted(Comparator.comparing(Teacher::getId))
                .forEach(teacher -> {
                    TeacherCapacity capacity = new TeacherCapacity();
                    capacity.setTeacherId(teacher.getId());
                    capacity.setTeacherName(teacher.getTeacherName());
                    capacity.setMaxWeeklyPeriods(maxWeeklyPeriods);
                    capacities.add(capacity);
                });
        return capacities;
    }
}
