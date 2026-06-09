package org.ideoholic.timetable.engine.context;

import org.ideoholic.timetable.entity.Period;
import org.ideoholic.timetable.entity.Section;
import org.ideoholic.timetable.entity.Subject;
import org.ideoholic.timetable.entity.Teacher;
import org.ideoholic.timetable.entity.WorkingDay;

import lombok.Data;

@Data
public class TimetableContext {

    private Teacher teacher;

    private Subject subject;

    private Section section;

    private WorkingDay workingDay;

    private Period period;
}