package org.ideoholic.timetable.engine.generation;

import java.util.List;

import org.ideoholic.timetable.entity.Period;
import org.ideoholic.timetable.entity.Section;
import org.ideoholic.timetable.entity.Teacher;
import org.ideoholic.timetable.entity.WorkingDay;

public class TimetableGenerationPlan {

    private final List<Section> sections;

    private final List<WorkingDay> workingDays;

    private final List<Period> periods;

    private final List<Teacher> teachers;

    public TimetableGenerationPlan(
            List<Section> sections,
            List<WorkingDay> workingDays,
            List<Period> periods,
            List<Teacher> teachers) {

        this.sections = sections;
        this.workingDays = workingDays;
        this.periods = periods;
        this.teachers = teachers;
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<WorkingDay> getWorkingDays() {
        return workingDays;
    }

    public List<Period> getPeriods() {
        return periods;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }
}
