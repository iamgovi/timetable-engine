package org.ideoholic.timetable.repository;

import java.util.List;

import org.ideoholic.timetable.entity.Period;
import org.ideoholic.timetable.entity.Section;
import org.ideoholic.timetable.entity.Subject;
import org.ideoholic.timetable.entity.Teacher;
import org.ideoholic.timetable.entity.TimetableAssignment;
import org.ideoholic.timetable.entity.WorkingDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimetableAssignmentRepository
        extends JpaRepository<TimetableAssignment, Long> {

    long countByTeacherAndWorkingDay(
            Teacher teacher,
            WorkingDay workingDay);

    long countBySectionAndSubject(
            Section section,
            Subject subject);

    long countBySectionAndSubjectAndWorkingDay(
            Section section,
            Subject subject,
            WorkingDay workingDay);

    TimetableAssignment findByTeacherAndWorkingDayAndPeriod(
            Teacher teacher,
            WorkingDay workingDay,
            Period period);

    boolean existsBySectionAndWorkingDayAndPeriod(
            Section section,
            WorkingDay workingDay,
            Period period);

    List<TimetableAssignment> findBySectionAndWorkingDayIdLessThan(
            Section section,
            Long workingDayId);

    boolean existsBySectionAndWorkingDayAndSubjectAndPeriodPeriodNumberLessThan(
            Section section,
            WorkingDay workingDay,
            Subject subject,
            Integer periodNumber);

    TimetableAssignment findBySectionAndWorkingDayAndPeriodPeriodNumber(
            Section section,
            WorkingDay workingDay,
            Integer periodNumber);

    boolean existsBySectionAndWorkingDayAndSubjectAndPeriodPeriodNumber(
            Section section,
            WorkingDay workingDay,
            Subject subject,
            Integer periodNumber);

    List<TimetableAssignment> findBySectionAndWorkingDay(
            Section section,
            WorkingDay workingDay);

    List<TimetableAssignment> findByWorkingDayAndPeriod(
            WorkingDay workingDay,
            Period period);

    TimetableAssignment findFirstBySectionAndPeriodAndWorkingDayIdLessThanOrderByWorkingDayIdDesc(
            Section section,
            Period period,
            Long workingDayId);

    void deleteBySectionInAndWorkingDayIdIn(
            List<Section> sections,
            List<Long> workingDayIds);
}
