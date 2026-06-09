package org.ideoholic.timetable.repository;

import org.ideoholic.timetable.entity.Period;
import org.ideoholic.timetable.entity.Teacher;
import org.ideoholic.timetable.entity.TeacherAvailability;
import org.ideoholic.timetable.entity.WorkingDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherAvailabilityRepository
        extends JpaRepository<TeacherAvailability, Long> {

    TeacherAvailability findByTeacherAndWorkingDayAndPeriod(
            Teacher teacher,
            WorkingDay workingDay,
            Period period);
}